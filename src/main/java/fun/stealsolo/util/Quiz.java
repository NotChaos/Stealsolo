package fun.stealsolo.util;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    @Getter
    @Setter
    private static String activeQuestion = null;
    @Getter
    private static final List<QuizQuestion> questions = new ArrayList<>();

    public static void randomizeQuestion() {
        if (activeQuestion != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Stealsolo.getAfkArea().isInArea(player.getLocation())) {
                    for (Component component : Stealsolo.getUnansweredMessage()) {
                        Message.successful(player, Message.replaceInComponent(Message.replaceInComponent(component, "%question%", activeQuestion), "%player%", player.getName()), false);
                    }

                    Bukkit.getScheduler().runTask(Stealsolo.getPlugin(), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getParticipationRewardCommand().replace("%player%", player.getName()));
                    });
                }
            }
        }

        if (questions.isEmpty()) {
            activeQuestion = null;
            return;
        }

        int randomIndex = (int) (Math.random() * questions.size());
        activeQuestion = questions.get(randomIndex).question();
    }

    public static void sendQuestion() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Stealsolo.getAfkArea().isInArea(player.getLocation())) {
                for (Component component : Stealsolo.getQuestionMessage()) {
                    Message.successful(player, Message.replaceInComponent(Message.replaceInComponent(component, "%question%", activeQuestion), "%player%", player.getName()), false);
                }
            }
        }
    }

    public static QuizQuestion getActiveQuizQuestion() {
        return questions.stream().filter(q -> q.question().equals(activeQuestion)).findFirst().orElse(null);
    }
}
