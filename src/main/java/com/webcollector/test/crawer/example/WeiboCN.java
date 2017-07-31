package com.webcollector.test.crawer.example;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * 利用Selenium获取登陆新浪微博weibo.cn的cookie
 *
 * @author hu
 */
public class WeiboCN {

    /**
     * 获取新浪微博的cookie，这个方法针对weibo.cn有效，对weibo.com无效 weibo.cn以明文形式传输数据，请使用小号
     *
     * @param username 新浪微博用户名
     * @param password 新浪微博密码
     * @return
     * @throws Exception
     */
    public static String getSinaCookie(String username, String password) throws Exception {
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        //driver.get("https://passport.weibo.cn/signin/login");
        driver.get("https://login.sina.com.cn/signup/signin.php");
       /* WebElement ele = driver.findElementByCssSelector("img");
        String src = ele.getAttribute("src");
        String cookie = concatCookie(driver);
        HttpRequest request = new HttpRequest(src);
        request.setCookie(cookie);
        HttpResponse response = request.response();
        ByteArrayInputStream is = new ByteArrayInputStream(response.content());
        BufferedImage img = ImageIO.read(is);
        is.close();
        ImageIO.write(img, "png", new File("result.png"));
        String userInput = new CaptchaFrame(img).getUserInput();
        System.out.println(userInput);*/
        WebElement mobile = driver.findElementByCssSelector("input[name=username]");
        mobile.sendKeys(username);
        WebElement pass = driver.findElementByCssSelector("input[name^=password]");
        pass.sendKeys(password);
       /* WebElement code = driver.findElementByCssSelector("input[name=code]");
        code.sendKeys(userInput);*/
        WebElement rem = driver.findElementByCssSelector("input[name=remLoginName]");
        rem.click();
        WebElement submit = driver.findElementByCssSelector("input[type=submit]");
        submit.click();
        String result = concatCookie(driver);
        driver.close();
        if (result.contains("SUB")) {
            return result;
        } else {
            throw new Exception("weibo login failed");
        }
    }

    public static String concatCookie(HtmlUnitDriver driver) {
        Set<Cookie> cookieSet = driver.manage().getCookies();
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieSet) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        String result = sb.toString();
        return result;
    }

    public static class CaptchaFrame {

        JFrame frame;
        JPanel panel;
        JTextField input;
        int inputWidth = 100;
        BufferedImage img;
        String userInput = null;

        public CaptchaFrame(BufferedImage img) {
            this.img = img;
        }

        public String getUserInput() {
            frame = new JFrame("输入验证码");
            final int imgWidth = img.getWidth();
            final int imgHeight = img.getHeight();
            int width = imgWidth * 2 + inputWidth * 2;
            int height = imgHeight * 2+50;
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int startx = (dim.width - width) / 2;
            int starty = (dim.height - height) / 2;
            frame.setBounds(startx, starty, width, height);
            Container container = frame.getContentPane();
            container.setLayout(new BorderLayout());
            panel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, imgWidth * 2, imgHeight * 2, null);
                }
            };
            panel.setLayout(null);
            container.add(panel);
            input = new JTextField(6);
            input.setBounds(imgWidth * 2, 0, inputWidth, imgHeight * 2);
            panel.add(input);
            JButton btn = new JButton("登录");
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userInput = input.getText().trim();
                    synchronized (CaptchaFrame.this) {
                        CaptchaFrame.this.notify();
                    }
                }
            });
            btn.setBounds(imgWidth * 2 + inputWidth, 0, inputWidth, imgHeight * 2);
            panel.add(btn);
            frame.setVisible(true);
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            frame.dispose();
            return userInput;
        }
    }
    
    
    public static void main(String[] args) throws Exception {
    	WeiboCN.getSinaCookie("18600518239", "199009@yang");
	}

}