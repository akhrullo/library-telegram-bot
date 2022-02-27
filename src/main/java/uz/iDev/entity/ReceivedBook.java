package uz.iDev.entity;


import lombok.*;
import uz.iDev.enums.state.BookState;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedBook extends Book {
    private String fileId;
    private String ownerId;
    private BookState state;


}
