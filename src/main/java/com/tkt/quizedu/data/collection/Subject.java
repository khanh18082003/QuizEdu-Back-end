package com.tkt.quizedu.data.collection;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;

@Document(collection = "subjects")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Subject extends StringIdentityCollection {
    @Serial
    private static final long serialVersionUID = -5465733518693373245L;
    @Id
    String id;
    @Indexed(unique = true)
    String name;
}
