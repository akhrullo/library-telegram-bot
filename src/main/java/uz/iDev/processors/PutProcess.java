package uz.iDev.processors;

import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.InlineBoards;
import uz.iDev.buttons.MarkupBoard;
import uz.iDev.config.LangConfig;
import uz.iDev.config.PConfig;
import uz.iDev.entity.Book;
import uz.iDev.enums.state.AllState;
import uz.iDev.enums.state.BookState;
import uz.iDev.repository.LogRepository;
import uz.iDev.repository.bookRepository.ReceivedBookRepository;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static uz.iDev.FreePdfBot.users;
import static uz.iDev.config.State.setAllState;
import static uz.iDev.handlers.MessageHandler.book;

public class PutProcess {
    private static final PutProcess instance = new PutProcess();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    public static final Map<String, BookState> bookStatus = new HashMap<>();
    public static  BookState bookState;
    public static final ReceivedBookRepository instanceReceivedBook= ReceivedBookRepository.getInstance();

    public void Put(Message message, AllState state) {
        String chatID = message.getChatId().toString();
        if (AllState.FULL_NAME.equals(state) || Objects.isNull(state)) {
            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.full.name.please"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
            LogRepository.getInstance().updateUser(chatID,AllState.AGE);
            LogRepository.getInstance().save(users.get(chatID), chatID, AllState.AGE.toString());
        } else if (AllState.AGE.equals(state)) {
            users.get(message.getChatId()+"").setFullName(message.getText());
            SendMessage sendMessage = new SendMessage(chatID, LangConfig.get(chatID, "your.age.please"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
            setAllState(chatID, AllState.GENDER);
            LogRepository.getInstance().updateUserName(chatID,message.getText());
            LogRepository.getInstance().updateUser(chatID,AllState.GENDER);;
        } else if (AllState.GENDER.equals(state)) {
            String text = message.getText();
            if (StringUtils.isNumeric(text)) {
                users.get(message.getChatId()+"").setAge(Integer.parseInt(text));
                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "gender"));
                sendMessage1.setReplyMarkup(InlineBoards.gender(chatID));
                BOT.executeMessage(sendMessage1);
                setAllState(chatID, AllState.PHONE_NUMBER);
                LogRepository.getInstance().updateUserAge(chatID,Integer.parseInt(text));
                LogRepository.getInstance().updateUser(chatID,AllState.PHONE_NUMBER);
            } else {
                SendMessage sendMessage1 = new SendMessage(chatID, LangConfig.get(chatID, "invalid.number.format") + LangConfig.get(chatID, "please.send.correct.number"));
                BOT.executeMessage(sendMessage1);
            }
        }else if(AllState.PHONE_NUMBER.equals(state)){
            String chatId= message.getChatId().toString();
            if (message.hasContact()) {
                users.get(
                        message.getContact().getUserId() + "").setPhoneNumber(message.getContact().getPhoneNumber());
                setAllState(chatId, AllState.AUTHORIZED);
                users.get(chatId).setUserName(message.getChat().getUserName());
                LogRepository.getInstance().updateUserPhoneNumber(chatID,message.getContact().getPhoneNumber());
                LogRepository.getInstance().updateUsername(chatID,message.getChat().getUserName());
                LogRepository.getInstance().updateUser(chatID,AllState.AUTHORIZED);
                SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "successfully.authorized"));
                sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
                BOT.executeMessage(sendMessage);
            }
            else {
                SendMessage sendMessage1 = new SendMessage(chatId, "Invalid Number format\nPlease send correct number");
                BOT.executeMessage(sendMessage1);
            }
       }
        else {
            DeleteMessage deleteMessage=new DeleteMessage(chatID,message.getMessageId());
            BOT.executeMessage(deleteMessage);
        }
    }


    public void setDescription(String chatId, String author){
        book.setAuthor(author);

        SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"book.category"));
        ReplyKeyboard replyKeyboard=InlineBoards.category(chatId);
        sendMessage.setReplyMarkup(replyKeyboard);
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookStateByChatId(chatId,BookState.CATEGORY);
    }
    public void setAuthor(String chatId, String text) {
        book.setDescription(text);
        SendMessage sendMessage= new SendMessage(chatId,LangConfig.get(chatId,"book.author"));
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookStateByChatId(chatId,BookState.AUTHOR);
    }

    public void sendMeBook(String chatId, Document document) {
        book=new Book();
        book.setId(document.getFileId());
        book.setOwnerId(chatId);
        book.setBookName(document.getFileName());
        instanceReceivedBook.save(document.getFileId(),chatId);
        SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"book.desc"));
        BOT.executeMessage(sendMessage);
        instanceReceivedBook.updateBookState(document.getFileId(),BookState.DESCRIPTION);
//        instanceReceivedBook.setBookState(document.getFileId(),chatId,BookState.DESCRIPTION);
    }
    public void putBooks(String chatId) {
            SendMessage sendMessage= new SendMessage(chatId,LangConfig.get(chatId,"book.send"));
            sendMessage.setReplyMarkup(new ForceReplyKeyboard());
            BOT.executeMessage(sendMessage);
    }



    public void forceReplyKeyboard(String chatID, String message) {
        SendMessage sendMessage = new SendMessage(chatID, message);
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        BOT.executeMessage(sendMessage);
    }

    public void languageChoice(String chatID) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatID);
        sendPhoto.setPhoto(new InputFile(new File(PConfig.get("bot.logo"))));
        BOT.executeMessage(sendPhoto);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText("Your language please");
        sendMessage.setReplyMarkup(InlineBoards.languageButtons());
        BOT.executeMessage(sendMessage);

    }
    public static PutProcess getInstance() {
        return instance;
    }
}
