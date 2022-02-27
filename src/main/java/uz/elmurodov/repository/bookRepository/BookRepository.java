package uz.elmurodov.repository.bookRepository;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.elmurodov.config.PConfig;
import uz.elmurodov.entity.Book;
import uz.elmurodov.repository.AbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class BookRepository extends AbstractRepository {

    public static void save(Book book) {
        String  sql="insert into books (id, bookName, description, Author, category,ownerId,size) values(?,?,?,?,?,?,?)";
        try (Connection connection= getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql)){
           preparedStatement.setString(1,book.getId());
           preparedStatement.setString(2,book.getBookName());
           preparedStatement.setString(3,book.getDescription());
           preparedStatement.setString(4,book.getAuthor());
           preparedStatement.setString(5,book.getCategory());
           preparedStatement.setString(6,book.getOwnerId());
           preparedStatement.setString(7,book.getSize()+"");
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void updateSize( String id, String size){
        String sql="update books set size = ? where id = ? ";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,size);
            preparedStatement.setString(2,id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void updateId( String id){
        String sql="update books set id = ?";
        try (Connection connection= getConnection();
        ){
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static List<Book> get(String message,Integer n) {
        List<Book>books=new ArrayList<>();
        String sql="select * from books  where books.ownerId like ?  or category like ? or bookName like  ? or author like ? limit 5 offset ? ";
        try (Connection connection= getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql)){

            preparedStatement.setString(1,message);
            preparedStatement.setString(2,"%"+message+"%");
            preparedStatement.setString(3,"%"+message+"%");
            preparedStatement.setString(4,"%"+message+"%");
            preparedStatement.setInt(5,n);

            ResultSet resultSet=preparedStatement.executeQuery();

            while(resultSet.next()){
                Book book=new Book();
                book.setAuthor(resultSet.getString("author"));
                book.setCategory(resultSet.getString("category"));
                book.setId(resultSet.getString("id"));
                book.setDescription(resultSet.getString("description"));
                book.setBookName(resultSet.getString("bookName"));
                book.setSize(resultSet.getString("size"));
                books.add(book);
            }
        } catch (SQLException throwAbles) {
            throwAbles.printStackTrace();
        }
        return books;
    }

    public static List<Book> getMY(String message) {
        List<Book>books=new ArrayList<>();
        String sql="select * from books  where books.ownerId like ? ";
        try (Connection connection= getConnection();
             PreparedStatement preparedStatement=connection.prepareStatement(sql)){

            preparedStatement.setString(1,message);
            ResultSet resultSet=preparedStatement.executeQuery();

            while(resultSet.next()){
                Book book=new Book();
                book.setAuthor(resultSet.getString("author"));
                book.setCategory(resultSet.getString("category"));
                book.setId(resultSet.getString("id"));
                book.setDescription(resultSet.getString("description"));
                book.setBookName(resultSet.getString("bookName"));
                books.add(book);
            }
        } catch (SQLException throwAbles) {
            throwAbles.printStackTrace();
        }
        return books;
    }
    public static String getSize(String Id) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(PConfig.get("books.select.item").formatted("size", Id));
            return resultSet.getString("size");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static @NonNull InputFile getFile(Book Id) {
//        try (Connection connection = getConnection();
//             Statement statement = connection.createStatement()) {
//            ResultSet resultSet = statement.executeQuery(PConfig.get("books.select.item").formatted("id", Id));
//            return resultSet.getString("id");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



}
