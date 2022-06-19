package hello.jdbc.connection.repository;

import hello.jdbc.connection.DBCConnectionUtil;
import hello.jdbc.connection.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV0 {

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // select 시 사용하는 쿼리

            // 커서라는게 존재함 -> 첫번째 데이터가 존재하면 true 없으면 false
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberID = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        } finally {
            close(connection, pstmt, rs);
        }
    }


    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        con = getConnection();
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // <= 쿼리가 실행돰
            return member;
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("db error", e);
            throw e;
        } finally {
            // 외부 리소스를 쓰는것이기때문에 커넥션을 닫아줘여함 계속해서 유지가 되기때문에 ( 무조건 닫아줘야함 )
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet resultSet) {

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBCConnectionUtil.getConnection();
    }
}
