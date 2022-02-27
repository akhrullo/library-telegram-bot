package uz.iDev.services;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import uz.iDev.FreePdfBot;
import uz.iDev.buttons.MarkupBoard;
import uz.iDev.config.LangConfig;
import uz.iDev.config.PConfig;
import uz.iDev.emojis.Emojis;
import uz.iDev.handlers.MessageHandler;

import java.io.File;

/**
 * @author Narzullayev Husan, вт 23:20. 28.12.2021
 */
public class ContactService {
    private static final ContactService instance=new ContactService();
    private static final MessageHandler messageHandler = MessageHandler.getInstance();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();

    public void contactUs( String chatId) {
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(chatId);
        sendAnimation.setAnimation(new InputFile(new File(PConfig.get("contact.us.sticker"))));
        BOT.executeMessage(sendAnimation);
        SendMessage sendMessage = new SendMessage(chatId, Emojis.ADMINS + LangConfig.get(chatId, "admins") + "\uD83D\uDC47\n" +
                "https://t.me/Narzullayev_Husan\n" +
                "https://t.me/akhrullo\n" +
                "https://t.me/AzizaTojiboeva\n" +
                "https://t.me/Uchqun99bek26\n");
        sendMessage.setReplyMarkup( MarkupBoard.contactUs(chatId));
        BOT.executeMessage(sendMessage);
    }

    public static ContactService getInstance() {
        return instance;
    }
}
