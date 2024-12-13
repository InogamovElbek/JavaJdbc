package org.example;//package org.example;
//
//import lombok.SneakyThrows;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import java.sql.*;
//
//public class MyBot extends TelegramLongPollingBot {
//    DatabaseConfig dbConnection = new DatabaseConfig();
//    @Override
//    @SneakyThrows
//    public void onUpdateReceived(Update update) {
//
//        if (update.hasMessage()) {
//            Message message = update.getMessage();
//            Long chatId = message.getChatId();
//            String text = message.getText();
//            String[] words = text.split(" ");
//
//            if (words[0].equals("/list")) {
//                String products = getProducts();
//
//                SendMessage sendMessage = SendMessage.builder()
//                        .chatId(chatId)
//                        .text(products)
//                        .build();
//
//                execute(sendMessage);
//
//            } else if (words[0].equals("/add")) {
//                if (words.length >= 3) {
//                    String name = words[1];
//                    try {
//                        double price = Double.parseDouble(words[2]);
//                        createProduct(name, price);
//                        sendMessage(chatId, "Mahsulot muvaffaqiyatli qoshldi");
//                    } catch (NumberFormatException e) {
//                        sendMessage(chatId, "Narx notogri formatda Narxni son shaklida kiriting.");
//                    }
//                } else {
//                    sendMessage(chatId, "Please mahsulot nomi va narxini kiriting. Masalan: /add Olma 5000");
//                }
//            } else {
//                sendMessage(chatId, "NImadur xatode karcho! Foydalanish: /list yoki /add [mahsulot nomi] [narx]");
//            }
//        }
//    }
//    private void createProduct(String name, double price) {
//        String sql = "insert into products (name, price) VALUES (?, ?)";
//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, name);
//            statement.setDouble(2, price);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private String getProducts() {
//        String sql = "select id, name, price from products";
//
//        try (Connection connection = dbConnection.getConnection();
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(sql)) {
//
//            StringBuilder builder = new StringBuilder("Mahsulotlar royxati:\n");
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                double price = resultSet.getDouble("price");
//
//                builder.append(id).append(" | ").append(name).append(" | ").append(price).append(" som\n");
//            }
//
//            return builder.toString();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(text);
//
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return "@yayayay";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "7877821768:AAHkMKmzixa_X8GUylEsHqMgg1Qzk6cFVj0";
//    }
//}









import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

public class MyBot extends TelegramLongPollingBot {
    DatabaseConfig dbConnection = new DatabaseConfig();

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            String[] words = text.split(" ");

            switch (words[0]) {
                case "/list":
                    String products = getProducts();
                    sendMessage(chatId, products);
                    break;

                case "/add":
                    if (words.length >= 3) {
                        String name = words[1];
                        try {
                            double price = Double.parseDouble(words[2]);
                            createProduct(name, price);
                            sendMessage(chatId, "Mahsulot muvaffaqiyatli qoshildi.");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "Narx noto'g'ri formatda. Narxni son shaklida kiriting.");
                        }
                    } else {
                        sendMessage(chatId, "Iltimos, mahsulot nomi va narxini kiriting. Masalan: /add Olma 5000");
                    }
                    break;

                case "/update":
                    if (words.length >= 4) {
                        try {
                            int id = Integer.parseInt(words[1]);
                            String name = words[2];
                            double price = Double.parseDouble(words[3]);
                            updateProduct(id, name, price);
                            sendMessage(chatId, "Mahsulot muvaffaqiyatli yangilandi.");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "ID yoki narx noto'g'ri formatda. ID va narxni son shaklida kiriting.");
                        }
                    } else {
                        sendMessage(chatId, "Iltimos, mahsulot ID, nomi va narxini kiriting. Masalan: /update 1 Olma 6000");
                    }
                    break;

                case "/delete":
                    if (words.length >= 2) {
                        try {
                            int id = Integer.parseInt(words[1]);
                            deleteProduct(id);
                            sendMessage(chatId, "Mahsulot ochdi.");
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "ID notori formatda. IDni son shaklida kiriting.");
                        }
                    } else {
                        sendMessage(chatId, "Iltims, mahsulot ID ni kiriting. Masalan: /delete 1");
                    }
                    break;

                default:
                    sendMessage(chatId, "Nimadir xatode karochi! Foydalanish: /list, /add, /update yoki /delete [parametrlar]");
                    break;
            }
        }
    }

  void createProduct(String name, double price) {
        String sql = "insert into products (name, price)  values (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stat= connection.prepareStatement(sql)) {
            stat.setString(1, name);
            stat.setDouble(2, price);
            stat.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

  void updateProduct(int id, String name, double price) {
        String sql = "update products set name = ?, price = ? WHERE id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stat = connection.prepareStatement(sql)) {
            stat.setString(1, name);
            stat.setDouble(2, price);
            stat.setInt(3, id);
            stat.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//     void deleteProduct(int id) {
//        String sql = "DELETE FROM products WHERE id = ?";
//        try (Connection connection = dbConnection.getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setInt(1, id);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }



    void deleteProduct(int id) {
//        String sql = "DELETE FROM Product WHERE id = ?";
//        try (Connection connection = dbConnection.getConnection();//autoClosable
//             PreparedStatement statement = connection.prepareStatement(sql);) {
//            int res = statement.executeUpdate(sql);
//            System.out.println(res);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

        String sql = "delete from Product where id = " + id + ";";
        try (Connection connection = dbConnection.getConnection();
             Statement stat = connection.createStatement()) {
            int res = stat.executeUpdate(sql);
            System.out.println(res);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    String getProducts() {
        String sql = "select id, name, price from products";
        try (Connection connection = dbConnection.getConnection();
             Statement stat = connection.createStatement();
             ResultSet tSet = stat.executeQuery(sql)) {

            StringBuilder builder = new StringBuilder("Mahsulotlar ro'yxati:\n");
            while (tSet.next()) {
                int id = tSet.getInt("id");
                String name = tSet.getString("name");
                double price = tSet.getDouble("price");

                builder.append(id).append(" | ").append(name).append(" | ").append(price).append(" so'm\n");
            }
            return builder.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "@yayayay";
    }

    @Override
    public String getBotToken() {
        return "7877821768:AAHkMKmzixa_X8GUylEsHqMgg1Qzk6cFVj0";
    }
}