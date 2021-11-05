package net.silthus.chat.targets;

import net.kyori.adventure.text.Component;
import net.silthus.chat.ChatTarget;
import net.silthus.chat.Message;

public class DirectConversation extends AbstractConversation {

    public DirectConversation(ChatTarget target1, ChatTarget target2) {
        super(target1.getIdentifier() + "#" + target2.getIdentifier());
        setName(Component.text("<partner_name>"));
        addTarget(target1);
        addTarget(target2);
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
