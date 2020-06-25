package kub4k1.bookmanagement.adapter.outgoing.user.tokenSender;

import kub4k1.bookmanagement.domain.user.dto.exception.CannotSendTokenException;
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Service
class TokenSenderEmail implements TokenSender {

    private final JavaMailSender javaMailSender;

    @Value("${app.path}")
    private String appPath;

    @Override
    public void sendToken(String token, String receiver) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);

            mimeMessageHelper.setTo(receiver);
            mimeMessageHelper.setSubject("Activate your account on LibraryApp");
            mimeMessageHelper.setText("Activation link: " + appPath + "/auth/activation?token=" + token );

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CannotSendTokenException(e.getMessage());
        }
    }

}
