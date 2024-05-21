package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DailyRepeatDTO.class, name = "DAILY"),
        @JsonSubTypes.Type(value = WeeklyRepeatDTO.class, name = "WEEKLY")
})
public interface RepeatStrategyDTO {
    RepeatType getRepeatType();
}
