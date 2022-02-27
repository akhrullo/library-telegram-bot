package uz.iDev.processors;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import uz.iDev.FreePdfBot;
import uz.iDev.config.LangConfig;
import uz.iDev.entity.Book;
import uz.iDev.enums.state.BookState;
import uz.iDev.services.OtherService;
import uz.iDev.services.SearchService;
import java.util.HashMap;
import java.util.Map;

import static uz.iDev.handlers.MessageHandler.isSearching;

/**
 * @author Narzullayev Husan, пт 10:23. 31.12.2021
 */
public class SearchProcess {
    private static final SearchProcess instance=new SearchProcess();
    private static final OtherService otherService = OtherService.getInstance();
    private static final SearchService searchService = SearchService.getInstance();
    private static final PutProcess putProcess = PutProcess.getInstance();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    public static Book book;
    public static final Map<String, BookState> bookStatus = new HashMap<>();

    public void search(Message message){
        String chatId=message.getChatId().toString();
        SendMessage sendMessage = new SendMessage(chatId,LangConfig.get(chatId,"search.report") );
        sendMessage.setReplyMarkup(new ForceReplyKeyboard());
        BOT.executeMessage(sendMessage);
        isSearching = true;
    }


    public static SearchProcess getInstance() {
        return instance;
    }

}
