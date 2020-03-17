package com.ecommerce.ecommercecart.database;

import com.ecommerce.ecommercecart.model.Cart;

import java.sql.*;

public class SqliteDB {

    private static Connection connection;
    private static boolean hasData = false;


    public ResultSet getCartData(String user) throws ClassNotFoundException, SQLException {

        if(connection == null)
            getConnection();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select sum(quantity) as quantity, name, itemid from cart where user='"+user+"' group by itemid,name,user");

//        deleteCartData(user);
        return resultSet;
    }

    public void addCartData(Cart cart) throws ClassNotFoundException, SQLException{

        if(connection == null)
            getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO cart values(?,?,?,?)");
        preparedStatement.setInt(1,cart.getItemid());
        preparedStatement.setString(2,cart.getName());
        preparedStatement.setInt(3,cart.getQuanity());
        preparedStatement.setString(4,cart.getUser());
        preparedStatement.execute();
    }

    public void deleteCartData(String user)throws ClassNotFoundException, SQLException{
        if(connection == null)
            getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from cart");
    }

    private void getConnection() throws  ClassNotFoundException, SQLException{

        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:cartdb.db");
        initialize();
    }

    private void initialize() throws  ClassNotFoundException, SQLException{
        if(!hasData)
            hasData = true;

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='cart'");

        if( !resultSet.next()){
            Statement statement1 = connection.createStatement();
            statement1.execute("CREATE TABLE cart(itemid integer,"
                    +"name varchar(100),"
                    +"quantity integer,"
                    +"user varchar(20));");
        }
    }
}

