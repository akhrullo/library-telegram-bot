package uz.iDev.buttons;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.iDev.FreePdfBot;
import uz.iDev.config.LangConfig;
import uz.iDev.emojis.Emojis;
import uz.iDev.entity.Auditable;
import uz.iDev.entity.Book;
import uz.iDev.entity.Comment;
import uz.iDev.repository.bookRepository.BookRepository;
import uz.iDev.services.OtherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class utilsInlineBoard {
    private static final InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
    private static final OtherService otherService = OtherService.getInstance();

    public static void next(List<Book> listEntity, String chatId) {
        String message = takeMessage(listEntity);
        List<List<InlineKeyboardButton>> lists = createButtonSearch(listEntity);

        if (listEntity.size() == 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton("➡️next");
            inlineKeyboardButton.setCallbackData("next");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);
        }
        if (listEntity.size() == 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(Emojis.PREV+ LangConfig.get(chatId,"prev"));
            inlineKeyboardButton.setCallbackData("prev");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);
        }

        inline.setKeyboard(lists);
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.setReplyMarkup(inline);
        FreePdfBot.getInstance().executeMessage(sendMessage);
    }


    public static void nextForComment(List<Comment> listEntity, String chatId) {
        String message = getComment(listEntity);
        List<List<InlineKeyboardButton>> lists = createButton(listEntity);

        if (listEntity.size() >= 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(Emojis.NEXT+LangConfig.get(chatId,"next"));
            inlineKeyboardButton.setCallbackData("nextForComment");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);

        }
        if (listEntity.size() <= 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(Emojis.PREV+ LangConfig.get(chatId,"prev"));
            inlineKeyboardButton.setCallbackData("prevForComment");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);
        }

        inline.setKeyboard(lists);
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.setReplyMarkup(inline);
        FreePdfBot.getInstance().executeMessage(sendMessage);

    }

    public static void nextMyBooks(List<Book> listEntity, String chatId) {
        String message = takeMessage(listEntity);
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        if (listEntity.size() >= 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton("➡️next");
            inlineKeyboardButton.setCallbackData("next");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);
        }
        if (listEntity.size() == 5) {
            List<InlineKeyboardButton> buttons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(Emojis.PREV+ LangConfig.get(chatId,"prev"));
            inlineKeyboardButton.setCallbackData("prev");
            buttons1.add(inlineKeyboardButton);
            lists.add(buttons1);
        }
        inline.setKeyboard(lists);
        SendMessage sendMessage = new SendMessage(chatId, message);
        sendMessage.setReplyMarkup(inline);
        FreePdfBot.getInstance().executeMessage(sendMessage);
    }


    private static String takeMessage(List<Book> listEntity) {
        StringBuilder returns = new StringBuilder();
        for (int i = 1; i <= listEntity.size(); i++) {
            double size = Math.round(Double.parseDouble(Objects.requireNonNull(
                    BookRepository.getSize(listEntity.get(i - 1).getId()))) / 1048576 * 100.0) / 100.0;
            String hajm = " MB";
            returns.append(i + "\uFE0F\u20E3").append(". ").append(listEntity.get(i - 1).getBookName()).
                    append("\n  ✍️Author:   ").append(listEntity.get(i - 1).getAuthor()).
                    append("\n \uD83D\uDCBE Size: ").append(size).append(hajm).append("\n\n");
        }
        return returns.toString();
    }

    private static <E extends Auditable> List<List<InlineKeyboardButton>> createButton(List<E> listEntity) {
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        for (int i = 0; i < listEntity.size(); i++) {
            if (i >= 5) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton((i + 1) + "\uFE0F\u20E3");
                inlineKeyboardButton.setCallbackData((i * 10 + 10) + "");
                buttons2.add(inlineKeyboardButton);
            } else {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton((i + 1) + "\uFE0F\u20E3");
                inlineKeyboardButton.setCallbackData((i * 10 + 10) + "");
                buttons1.add(inlineKeyboardButton);
            }
        }

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        lists.add(buttons1);
        lists.add(buttons2);
        return lists;

    }

    private static <E extends Auditable> List<List<InlineKeyboardButton>> createButtonSearch(List<E> listEntity) {
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        for (int i = 0; i < listEntity.size(); i++) {
            if (i >= 5) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton((i + 1) + "\uFE0F\u20E3");
                inlineKeyboardButton.setCallbackData(i + "");
                buttons2.add(inlineKeyboardButton);
            } else {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton((i + 1) + "\uFE0F\u20E3");
                inlineKeyboardButton.setCallbackData(i + "");
                buttons1.add(inlineKeyboardButton);
            }
        }

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        lists.add(buttons1);
        lists.add(buttons2);
        return lists;

    }

    private static String getComment(List<Comment> listEntity) {
        StringBuilder returns = new StringBuilder();
        for (int i = 1; i <= listEntity.size(); i++) {
            returns.append(i + "\uFE0F\u20E3").append(". ").append(listEntity.get(i - 1).getFullName()).
                    append("  :  ").append(listEntity.get(i - 1).getComment()).append("\n");
        }
        return returns.toString();
    }
}