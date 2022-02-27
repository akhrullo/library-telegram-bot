package uz.elmurodov.processors;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import uz.elmurodov.FreePdfBot;
import uz.elmurodov.buttons.MarkupBoard;
import uz.elmurodov.config.LangConfig;
import uz.elmurodov.entity.Book;
import uz.elmurodov.enums.state.AllState;
import uz.elmurodov.enums.state.BookState;
import uz.elmurodov.enums.state.SearchState;
import uz.elmurodov.services.OtherService;
import uz.elmurodov.services.SearchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static uz.elmurodov.config.State.setSearchState;
import static uz.elmurodov.handlers.MessageHandler.isSearching;

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
