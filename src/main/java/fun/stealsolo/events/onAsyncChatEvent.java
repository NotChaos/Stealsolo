package fun.stealsolo.events;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Quiz;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class onAsyncChatEvent implements Listener {


    @EventHandler
    public void AsyncPlayerChat(AsyncChatEvent e) {
        if (!Stealsolo.getAfkArea().isInArea(e.getPlayer().getLocation())) {
            return;
        }

        e.setCancelled(true);

        if (!Quiz.getActiveQuizQuestion().isValidAnswer(PlainTextComponentSerializer.plainText().serialize(e.originalMessage()))) {
            return;
        }

        Bukkit.getScheduler().runTask(Stealsolo.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!Stealsolo.getAfkArea().isInArea(player.getLocation())) {
                    return;
                }

                if (player != e.getPlayer()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getParticipationRewardCommand().replace("%player%", e.getPlayer().getName()));
                }

                for (Component component : Stealsolo.getAnsweredMessage()) {
                    Message.successful(player, Message.replaceInComponent(Message.replaceInComponent(component, "%question%", Quiz.getActiveQuestion()), "%player%", player.getName()), false);
                }

            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getCorrectAnswerRewardCommand().replace("%player%", e.getPlayer().getName()));
        });

        Quiz.setActiveQuestion(null);
    }
}
