import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    public Database(){

    }

    public ResultSet getAll() throws SQLException {
        this.openConnection();

        this.resultSet = this.statement.executeQuery(
                "SELECT * FROM mots"
        );

        return this.resultSet;
    }

    public void openConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/pendu",
                    "pendu", "PENDU"
            );

            this.statement = this.connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public void closeConnection() throws SQLException {
        this.resultSet.close();
        this.statement.close();
        this.connection.close();
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
}
