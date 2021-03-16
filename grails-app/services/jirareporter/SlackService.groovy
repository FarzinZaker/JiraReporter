package jirareporter


import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import grails.gorm.transactions.Transactional

@Transactional
class SlackService {

    def post(String slackId, String message) {

        Slack slack = Slack.getInstance();
        ChatPostMessageResponse resp = slack.methods(Configuration.slackToken)
                .chatPostMessage(ChatPostMessageRequest.builder()
                        .channel(slackId)
                        .text(message).build())
        resp

    }
}
