package uz.iDev.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.InlineBoards;
import uz.iDev.config.LangConfig;
import uz.iDev.emojis.Emojis;
import uz.iDev.repository.authuser.AuthUserRepository;


/**
 * @author Narzullayev Husan, вс 10:26. 02.01.2022
 */
public class SettingService {
    private static final SettingService instance = new SettingService();

    private static final FreePdfBot BOT = FreePdfBot.getInstance();


    public void setting(String chatId) {

        SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "settings1") +
                "\n\n" + Emojis.NAME + "<strong>" +
                LangConfig.get(chatId, "settings2") + "</strong>" + "<i>" +
                "  " + AuthUserRepository.getInstance().getFullName(chatId) + "</i>" +
                "\n\n" + Emojis.AGE + "<strong>" +
                LangConfig.get(chatId, "settings3") + "</strong>" + "<i>" +
                "  " + AuthUserRepository.getInstance().getAge(chatId) + "</i>" +
                "\n\n" + Emojis.LANGUAGE + "<strong>" +
                LangConfig.get(chatId, "settings4") + "</strong>" + "<i>" +
                "  " + AuthUserRepository.getInstance().getLanguage(chatId) + "</i>" +
                "\n\n" +
                LangConfig.get(chatId, "settings5") +
                "\n");
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(InlineBoards.settings(chatId));
        BOT.executeMessage(sendMessage);
    }

    public static SettingService getInstance() {
        return instance;
    }
}
