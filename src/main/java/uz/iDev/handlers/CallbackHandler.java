package uz.iDev.handlers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.InlineBoards;
import uz.iDev.buttons.MarkupBoard;
import uz.iDev.config.LangConfig;
import uz.iDev.emojis.Emojis;
import uz.iDev.entity.Book;
import uz.iDev.entity.Comment;
import uz.iDev.enums.Language;
import uz.iDev.enums.state.AllState;
import uz.iDev.repository.LogRepository;
import uz.iDev.repository.bookRepository.BookRepository;
import uz.iDev.repository.bookRepository.ReceivedBookRepository;
import uz.iDev.repository.commentRepository.CommentRepository;
import uz.iDev.services.ContactService;
import uz.iDev.services.OtherService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static uz.iDev.FreePdfBot.users;
import static uz.iDev.buttons.utilsInlineBoard.*;
import static uz.iDev.config.GetAll.updates;
import static uz.iDev.config.State.*;
import static uz.iDev.handlers.MessageHandler.*;


/**
 * @author Elmurodov Javohir, Fri 6:48 PM. 12/17/2021
 */


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CallbackHandler {
    private static CallbackHandler instance = new CallbackHandler();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    public static Language language;
    private static final OtherService otherService = OtherService.getInstance();
    private static final ContactService contactService = ContactService.getInstance();
    public static Map<String, String> messages = new HashMap<>();
    public static Map<String, Integer> count = new HashMap<>();

    public void handle(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        String chatID = message.getChatId().toString();
        switch (data) {
            case "uz", "ru", "en" -> {
                System.out.println(data);
                DeleteMessage deleteMessage = new DeleteMessage(chatID, message.getMessageId());
                BOT.executeMessage(deleteMessage);
                users.get(chatID).setLanguage(data);
                SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "change.language"));
                BOT.executeMessage(sendMessage);
                otherService.menuExecte(chatID);
                updates(chatID, data);
            }
            case "male", "female" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, message.getMessageId());
                BOT.executeMessage(deleteMessage);
                SendMessage sendMessage = new SendMessage(chatID, Emojis.PHONE + LangConfig.get(chatID, "share.phone.number"));
                sendMessage.setReplyMarkup(MarkupBoard.sharePhoneNumber(chatID));
                BOT.executeMessage(sendMessage);
                users.get(chatID).setGender(callbackQuery.getData());
                LogRepository.getInstance().updateUserGender(chatID, callbackQuery.getData());
                setAllState(chatID, AllState.PHONE_NUMBER);
            }
            case "donate" -> {
                otherService.sendMessage(chatID, LangConfig.get(chatID, "pay"));
                contactService.contactUs(chatID);
            }
            case "science", "adventure", "history", "romance", "fantasy", "detective" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, message.getMessageId());
                BOT.executeMessage(deleteMessage);
                book.setCategory(data);
                SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "book.add"));
                sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatID));
                BOT.executeMessage(sendMessage);
                BookRepository.save(book);
                ReceivedBookRepository.getInstance().deleteData(chatID);
            }
            case "next" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, callbackQuery.getMessage().getMessageId());
                FreePdfBot.getInstance().executeMessage(deleteMessage);
                List<Book> book = BookRepository.get(messages.get(chatID), count.get(chatID) + 5);
                next(book, chatID);
                bookMap.put(chatID, book);
                count.put(chatID, count.get(chatID) + 5);
            }

            case "prev" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, callbackQuery.getMessage().getMessageId());
                FreePdfBot.getInstance().executeMessage(deleteMessage);
                List<Book> book = BookRepository.get(messages.get(chatID), count.get(chatID) - 5);

                next(book, chatID);
                bookMap.put(chatID, book);
                count.put(chatID, count.get(chatID) - 5);


            }
            case "cancel" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, callbackQuery.getMessage().getMessageId());
                FreePdfBot.getInstance().executeMessage(deleteMessage);
                otherService.sendMessage(chatID, LangConfig.get(chatID, "cancel"));
                otherService.menuExecte(chatID);
            }

            case "nextForComment" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, callbackQuery.getMessage().getMessageId());
                FreePdfBot.getInstance().executeMessage(deleteMessage);
                List<Comment> comments = CommentRepository.get((chatID), count.get(chatID) + 5);
                commentMap.put(chatID, comments);
                count.put(chatID, count.get(chatID) + 5);
                nextForComment(comments, chatID);

            }

            case "prevForComment" -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, callbackQuery.getMessage().getMessageId());
                FreePdfBot.getInstance().executeMessage(deleteMessage);
                List<Comment> comments = CommentRepository.get((chatID), count.get(chatID) - 5);
                commentMap.put(chatID, comments);

                count.put(chatID, count.get(chatID) - 5);
                nextForComment(comments, chatID);

            }

            case "10", "20", "30", "40", "50", "60", "70", "80", "90" -> {
                int index = Integer.parseInt(data) / 10 - 1;
                StringBuilder msg = new StringBuilder();
                msg = msg.append("<b> \uD83D\uDC64 ").append(commentMap.get(chatID).get(index).getFullName()).
                        append(" :  </b>\n\n\t\t <i> - ").append(commentMap.get(chatID).get(index).getComment()).append("</i>");
                SendMessage sendMessage = new SendMessage(chatID, msg.toString());
                sendMessage.enableHtml(true);
                FreePdfBot.getInstance().executeMessage(sendMessage);
            }

            case "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" -> {
                int index = Integer.parseInt(data);
                System.out.println(index);
                double size = Math.round(Double.parseDouble(Objects.requireNonNull(
                        BookRepository.getSize(bookMap.get(chatID).get((index)).getId()))) / 1048576 * 100.0) / 100.0;
                String caption = "??????" +
                        bookMap.get(chatID).get(index).getAuthor() + "\n\uD83D\uDCBE " +
                        size + " MB " +
                        "\n\n\uD83D\uDC49 @MavericksLibrarybot ";
                InputFile inputFile = new InputFile(bookMap.get(chatID).get(index).getId());
                SendDocument sendDocument = new SendDocument(chatID, inputFile);
                sendDocument.setCaption(caption);

                sendDocument.getDocument();
                System.out.println(sendDocument);
                BOT.executeMessage(sendDocument);
                OtherService.getInstance().menuExecte(chatID);

            }


            case "button1" -> {
                ///fullname
                SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.full.name.please"));
                sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                BOT.executeMessage(sendMessage);
                hasSettingsData = true;
                //otherService.menuExecte(chatID);
            }
            case "button2" -> {
                ///age
                SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.age.please"));
                sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                BOT.executeMessage(sendMessage);
                hasSettingsData = true;
                //  otherService.menuExecte(chatID);
            }
            case "button3" -> {
                ///language
                SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.language.please"));
                sendMessage.setReplyMarkup(InlineBoards.languageButtons());
                BOT.executeMessage(sendMessage);
                users.get(chatID).setLanguage(data);
                // otherService.menuExecte(chatID);
                updates(chatID, data);

            }
        }
    }

    public static CallbackHandler getInstance() {
        return instance;
    }
}
