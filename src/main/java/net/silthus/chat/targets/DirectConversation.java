package net.silthus.chat.targets;

import net.kyori.adventure.text.Component;
import net.silthus.chat.Chatter;
import net.silthus.chat.Message;

public class DirectConversation extends AbstractConversation {

    public DirectConversation(Chatter chatter1, Chatter chatter2) {
        super(chatter1.getIdentifier() + "#" + chatter2.getIdentifier());
        setName(Component.text("<partner_name>"));
        subscribe(chatter1);
        subscribe(chatter2);
    }

    @Override
    public void sendMessage(Message message) {
        addReceivedMessage(message);
        getTargets().stream()
                .filter(target -> !target.getConversations().contains(this))
                .forEach(target -> target.setActiveConversation(this));
        message.copy()
                .conversation(this)
                .targets(getTargets())
                .send();
    }
}
