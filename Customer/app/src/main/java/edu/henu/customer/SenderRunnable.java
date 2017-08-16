package edu.henu.customer;

import android.app.Notification;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

/**
 * Created by lenovo on 2017/8/11.
 */
public class SenderRunnable implements Runnable {

    private String user;
    private String password;
    private String subject;
    private String body;
    private String receiver;
    private MailSender sender;
    private String attachment;

    public SenderRunnable(String user, String password) {
        this.user = user;
        this.password = password;
        sender = new MailSender(user, password);
        /*String mailhost=user.substring(user.lastIndexOf("@")+1, user.lastIndexOf("."));
        */
        String mailhost="exmail.qq";
        if(!mailhost.equals("gmail")){
            mailhost="smtp."+mailhost+".com";
            Log.i("hello一条大河波浪宽的", mailhost);
            sender.setMailhost(mailhost);
        }
    }

    public void setMail(String subject, String body, String receiver,String attachment) {
        this.subject = subject;
        this.body = body;
        this.receiver = receiver;
        this.attachment=attachment;
    }

    public void run() {
        // TODO Auto-generated method stub
        try {
            sender.sendMail(subject, body, user, receiver,attachment);
            Log.e("Main","执行到这里了");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Main","执行到这里异常了",e);
        }
    }

}
