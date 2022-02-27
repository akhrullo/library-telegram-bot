package uz.elmurodov.handlers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.elmurodov.FreePdfBot;
import uz.elmurodov.buttons.MarkupBoard;
import uz.elmurodov.buttons.utilsInlineBoard;
import uz.elmurodov.config.GetAll;
import uz.elmurodov.config.LangConfig;
import uz.elmurodov.config.PConfig;
import uz.elmurodov.emojis.Emojis;
import uz.elmurodov.entity.Book;
import uz.elmurodov.entity.Comment;
import uz.elmurodov.enums.Language;
import uz.elmurodov.enums.state.AllState;
import uz.elmurodov.enums.state.BookState;
import uz.elmurodov.processors.PutProcess;
import uz.elmurodov.processors.SearchProcess;
import uz.elmurodov.repository.LogRepository;
import uz.elmurodov.repository.authuser.AuthUserRepository;
import uz.elmurodov.repository.bookRepository.BookRepository;
import uz.elmurodov.service.LogService;
import uz.elmurodov.services.*;
import uz.elmurodov.services.search.LanguageService;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.elmurodov.FreePdfBot.users;
import static uz.elmurodov.enums.state.AllState.findByText;
import static uz.elmurodov.handlers.CallbackHandler.count;
import static uz.elmurodov.handlers.CallbackHandler.messages;


/**
 * @author Elmurodov Javohir, Fri 6:44 PM. 12/17/2021
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageHandler {

    private static final MessageHandler instance = new MessageHandler();
    private static final LogService service = LogService.getInstance();
    private static final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
    private static final FreePdfBot BOT = FreePdfBot.getInstance();
    private static final OtherService otherService = OtherService.getInstance();
    private static final ContactService contactService = ContactService.getInstance();
    private static final StartAndOffService service1 = StartAndOffService.getInstance();
    private static final LanguageService languageService = LanguageService.getInstance();
    private static final SearchService searchService = SearchService.getInstance();
    private static final SearchProcess searchProcess = SearchProcess.getInstance();
    private static final PutProcess putProcess = new PutProcess();
    private static final LinkService linkService = LinkService.getInstance();
    private static final SettingService settingService = SettingService.getInstance();
    public static Language language;
    public static Book book=new Book();
    public static Comment comment;
    public static GetAll instanceGetAll=GetAll.getInstance();
    public static final Map<String, List<Book>>bookMap=new HashMap<>();
    public static final Map<String, List<Comment>> commentMap = new HashMap<>();
    public static boolean hasCom = false;
    public static boolean on = true;
    public static boolean isSearching = false;
    public static boolean hasSettingsData = false;
    public void handle(Message message) {

        service.create(message.getText());
        String chatId = message.getChatId().toString();
        Map<String ,String> map=instanceGetAll.getAllState();
        String text = map.get(chatId);
        AllState state=findByText(text);
        Map<String, String > map1=instanceGetAll.getBookState();
        String string=map1.get(chatId);
        BookState bookState=BookState.findBookStateByText(string);

        if(message.hasContact() && AllState.AGE.equals(state)){
            putProcess.Put(message,state);
        }

        if(message.hasDocument() && AllState.AUTHORIZED.equals(state)  && on ){
            putProcess.sendMeBook(chatId,message.getDocument());
            book.setId(message.getDocument().getFileId());
            book.setSize(message.getDocument().getFileSize().toString());

        }

        else if(BookState.DESCRIPTION.equals(bookState) && on){
            putProcess.setAuthor(chatId, message.getText());
        }

        else if(BookState.AUTHOR.equals(bookState) && on){
            putProcess.setDescription(chatId, message.getText());
        }

         if(AllState.AUTHORIZED.equals(state) && ("/start".equals(message.getText()))){
             otherService.sendMessage(chatId,LangConfig.get(chatId,"hi.bot")+"\n"+
                     LangConfig.get(chatId,"useful")+"\n"+
                     LangConfig.get(chatId,"use.off")+"\n");
            SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"welcome.to.our.library"));
            sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
            BOT.executeMessage(sendMessage);
        }
         else if (hasCom && on) {
             otherService.getComment(chatId, message);
         }

        else if (("/start".equals(message.getText())) && !AllState.AUTHORIZED.equals(state)) {
            service1.start(chatId);
            putProcess.languageChoice(chatId);
            SendMessage sendMessage = new SendMessage(chatId, "Welcome to our library");
            BOT.executeMessage(sendMessage);

        }

        else if (!on && !("/on".equals(message.getText()) ||
                 LangConfig.get(chatId, "restart").equals(message.getText()))){
             DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
             BOT.executeMessage(deleteMessage);
         }


        else if ("/off".equals(message.getText()) && on ) {
            service1.off(chatId);
            SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "subscription") +
                    "\n\n" +
                    LangConfig.get(chatId, "again"));
            sendMessage.setReplyMarkup(MarkupBoard.start(chatId));
            BOT.executeMessage(sendMessage);
            on=false;
        }

        else if ("/on".equals(message.getText()) || LangConfig.get(chatId, "restart").equals(message.getText())) {
            service1.on(chatId);
            String message1 = LangConfig.get(chatId, "on") +
                    "\n" +
                    LangConfig.get(chatId, "off") + "\n";
            SendMessage sendMessage = new SendMessage(chatId, message1);
            sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
            BOT.executeMessage(sendMessage);
            on=true;
        }

        else if (((Emojis.SEARCH+LangConfig.get(chatId, "search")).equals(message.getText()) ||
                 "/search".equals(message.getText())) && on ) {
            searchProcess.search(message);
        }
         else if (isSearching && on) {
             searchService.search(chatId, message.getText());
         }

        else if (((authUserRepository.isHaveUser(chatId) && (Emojis.ADD+LangConfig.get(chatId, "put")).equals(message.getText())) ||
                (authUserRepository.isHaveUser(chatId) && "/put".equals(message.getText()))) && on) {
           putProcess.putBooks(message.getChatId().toString());
        }
        else if((!authUserRepository.isHaveUser(chatId) && (Emojis.ADD+LangConfig.get(chatId,"put")).equals(message.getText()) ||
                (!authUserRepository.isHaveUser(chatId) && "/put".equals(message.getText())))  && on){
            putProcess.Put(message,state);
        }
        else if (("/contact_us".equals(message.getText()) ||
                (Emojis.CONTACT_US+LangConfig.get(chatId, "contact.us")).equals(message.getText()))  && on) {
            contactService.contactUs(chatId);
        }

        else if (("/language".equals(message.getText()) ||
                (Emojis.LANGUAGE+LangConfig.get(chatId, "language")).equals(message.getText()))  && on) {
            languageService.lang(chatId);
        }

        else if (("/links".equals(message.getText()) ||
                (Emojis.LINKS+LangConfig.get(chatId, "links")).equals(message.getText()))  && on) {
            linkService.link(chatId);
        }

         else if (("/comments".equals(message.getText()) ||
                 (Emojis.FEEDBACK + LangConfig.get(chatId, "subscribe.feedback") + Emojis.FEEDBACK).equals(message.getText()) && on)) {
             otherService.getComments(chatId);
         }

         else if (((Emojis.COMMENT + LangConfig.get(chatId, "comment")).equals(message.getText()) || "/report".equals(message.getText()))  && on) {
             otherService.report(chatId);
         }



        else if ("/users".equals(message.getText()) && on) {
            otherService.sendMessage(message, chatId, LangConfig.get(chatId, "users")+":  "+users.size());
        }

        else if ((Emojis.ABOUT_US+LangConfig.get(chatId, "about.us")).equals(message.getText())  && on) {
            otherService.aboutUs(chatId);
        }

        else if (("/help".equals(message.getText()) || (Emojis.HELP+LangConfig.get(chatId, "help")).equals(message.getText()))  && on) {
            otherService.help(chatId);
        }

         else if (("/settings".equals(message.getText()) ||
                 (Emojis.SETTINGS+LangConfig.get(chatId, "settings")).equals(message.getText()))  && on) {
             settingService.setting(chatId);
         }

         else if(hasSettingsData && message.hasText() && isAlphabeticMessage(message) && on){
             LogRepository.getInstance().updateUserName(chatId,message.getText());
             SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"full.name.changed"));
             sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
             BOT.executeMessage(sendMessage);
             hasSettingsData=true;
         }

         else if(hasSettingsData && !isAlphabeticMessage(message)  && on){
             LogRepository.getInstance().updateUserAge(chatId,Integer.parseInt(message.getText()));
             SendMessage sendMessage=new SendMessage(chatId,LangConfig.get(chatId,"age.changed"));
             sendMessage.setReplyMarkup(MarkupBoard.mainMenu(chatId));
             BOT.executeMessage(sendMessage);
             hasSettingsData=true;
         }

        else if ((Emojis.DONATE+LangConfig.get(chatId, "donate")).equals(message.getText()) && on ) {
            otherService.donate(chatId);
        }

        else if ((Emojis.CHANNEL+LangConfig.get(chatId, "channel")+Emojis.CHANNEL).equals(message.getText()) && on) {
            otherService.sendMessage(message, chatId, "https://t.me/PDFBOOKSYOUNEED");
        }

        else if ((Emojis.GROUP+LangConfig.get(chatId, "group")+Emojis.GROUP).equals(message.getText())  && on) {
            otherService.sendMessage(message, chatId, "https://t.me/+ertcIJalI4g5OWI6");
        }

        else if ((Emojis.GO_BACK+LangConfig.get(chatId, "go.back")).equals(message.getText()) && on) {
           otherService.menuExecte(chatId);
        }

        else if ("_Husan".equals(message.getText()) && on) {
            String name="husan";
            sendPhoto(chatId,name);
            otherService.sendMessage(message, chatId, "<a href=\"https://t.me/Narzullayev_Husan\"> Husan</a>");
        }

        else if ("_Axrullo".equals(message.getText())  && on) {
            String name="axrullo";
            sendPhoto(chatId,name);
            otherService.sendMessage(message, chatId, "<a href=\"https://t.me/akhrullo\">Axrullo</a>");
        }

        else if ("_Aziza".equals(message.getText())  && on) {
            String name="aziza";
            sendPhoto(chatId,name);
          otherService. sendMessage(message, chatId, "<a href=\"https://t.me/AzizaTojiboeva\">Aziza</a>");
        }

        else if ("_Uchqun".equals(message.getText())  && on) {
            String name="uchqun";
            sendPhoto(chatId,name);
           otherService.sendMessage(message, chatId, "<a href=\"https://t.me/Uchqun99bek26\">Uchqun</a>");
        }
        else if ((Emojis.MY_BOOKS +LangConfig.get(chatId,"my.books")).equals(message.getText())  && on) {
            count.put(chatId,0);
            List<Book>books= BookRepository.getMY(chatId);
             if (books.size() == 0) {
                 SendMessage sendMessage = new SendMessage(chatId, LangConfig.get(chatId, "book.not.found"));
                 BOT.executeMessage(sendMessage);
             }

            messages.put(chatId, chatId);
            utilsInlineBoard.nextMyBooks(books, chatId);


        }
        else {
            putProcess.Put(message,state);

        }
    }

    private void sendPhoto(String chatId,String  name) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new File(PConfig.get(name))));
        BOT.executeMessage(sendPhoto);
    }




    private boolean isAlphabeticMessage(Message message){
        if(message.hasText()){
            for (int i = 0; i < message.getText().length(); i++) {
                return Character.isAlphabetic(message.getText().charAt(i));
            }
            return false;
        }
        return false;
    }




    public static MessageHandler getInstance() {
        return instance;
    }


}
