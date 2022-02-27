package uz.iDev.services.search;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.InlineBoards;
import uz.iDev.config.LangConfig;

/**
 * @author Narzullayev Husan, пт 13:16. 31.12.2021
 */
public class LanguageService {
    private static final LanguageService instance=new LanguageService();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();

    public void lang(String chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(LangConfig.get(chatId, "your.language.please"));
        sendMessage.setReplyMarkup(InlineBoards.languageButtons());
        BOT.executeMessage(sendMessage);
    }

    public static LanguageService getInstance() {
        return instance;
    }
}
