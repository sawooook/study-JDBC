package hello.jdbc.connection.repository;

import hello.jdbc.connection.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;


@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Member findById(Connection connection, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
            // 커넥션은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }


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

    public void update(Connection connection, String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // <= 영향받은 쿼리수를 반환한다.
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }

    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate(); // <= 영향받은 쿼리수를 반환한다.
            log.info("resultSize = {}", resultSize);
        } catch (SQLException e) {
            log.error("db error ", e);
            throw e;
        } finally {
            close(connection, pstmt, rs);
        }

    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int resultSize = pstmt.executeUpdate(); // <= 영향받은 쿼리수를 반환한다.
            log.info("resultSize = {}", resultSize);
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
        // 해당 유틸안에 connection, prepareStatement, resultSet을 종료시키는 코드가 전부들어가있음
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("get connection = {}, class = {}", connection, connection.getClass());

        return connection;
    }
}

