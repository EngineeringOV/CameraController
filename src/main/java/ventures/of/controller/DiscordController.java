package ventures.of.controller;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import ventures.of.model.CameraEnvironment;
import ventures.of.util.EnvironmentVariableUtil;
import ventures.of.util.StringUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

//todo bulk set settings through discord

@Slf4j
public class DiscordController extends ListenerAdapter {
    private final String IMAGES_DIR = EnvironmentVariableUtil.getPropertyString("camera.settings.function.image.dir");

    private final MasterController masterController;
    public DiscordController(MasterController masterController, String token) {
        this.masterController = masterController;
        JDABuilder.createDefault(token)
                .addEventListeners(this)
                .setActivity(Activity.playing("Commands = !help"))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        log.info("Discord bot is ready!");
    }

    //todo clean this S%%Tuff up
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        new Thread(() -> {
            Message message = event.getMessage();
            if (/*!event.isFromType(ChannelType.TEXT) &&*/ !event.isFromType(ChannelType.PRIVATE)) {
                return;
            }
            else if (event.getAuthor().isBot() /* && event.getAuthor().getName().equals() */) {
                return;
            }


            String userId = event.getAuthor().getId();

            PrivateChannel privateChannel = message.getChannel().asPrivateChannel();
            String messageContent = event.getMessage().getContentRaw();
            privateChannel.sendMessage("Message received, processing").queue();
            if (messageContent.equalsIgnoreCase("!help")) {
                String str = userId+"! Here is the current commands\n" + "!killCam / kc\n" +
                        "!settings\n" +
                        "!snap / s\n" +
                        "!timelapse / tl\n" +
                        "!video / v\n" +
                        "!left / v\n" +
                        "!right / v\n" +
                        "!current / v\n" +
                        "!action / v\n" +
                        "!latestImg / l\n"+
                        "!left / dl\n"+
                        "!right / dr\n"+
                        "!current  \n"+
                        "!action / !click\n" +
                        event.getAuthor().getEffectiveName() +"\n" +
                        event.getAuthor().getName();
                privateChannel.sendMessage(str).queue();
            }
            else if (messageContent.equalsIgnoreCase("!settings")) {
                privateChannel.sendMessage( masterController.cameraController.getCs().toJson());
            }

            else if (messageContent.equalsIgnoreCase("!killCam") || messageContent.equalsIgnoreCase("kc")) {
                CameraController.killLibCamera();
                privateChannel.sendMessage("It's dead").queue();
            }
            else if (messageContent.equalsIgnoreCase("!snap") || messageContent.equalsIgnoreCase("s")) {
                masterController.cameraController.triggerTakeStill(8000,false);
                privateChannel.sendMessage("SNAP!").queue();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if (messageContent.equalsIgnoreCase("!timelapse") || messageContent.equalsIgnoreCase("tl")) {
                CameraController.killLibCamera();
                privateChannel.sendMessage("Timelapse is going!").queue();
                masterController.cameraController.triggerTimelapse();
            }
            else if (messageContent.equalsIgnoreCase("!video") || messageContent.equalsIgnoreCase("v")) {
                CameraController.killLibCamera();
                privateChannel.sendMessage("Video is going!").queue();
                masterController.cameraController.triggerVideo();
            }
            else if (messageContent.equalsIgnoreCase("!screenshot") || messageContent.equalsIgnoreCase("ss")) {
                InputStream inputStream;
                try {
                    File image = new File(screenshot());
                    inputStream = new FileInputStream(image);
                    FileUpload fileUpload = FileUpload.fromData(inputStream, "screenshot.png");
                    privateChannel.sendFiles(fileUpload).queue();
                } catch (IOException | AWTException e) {
                    privateChannel.sendMessage("No screenshot file right now!").queue();
                    e.printStackTrace();
                }
            }
            else if (messageContent.equalsIgnoreCase("!latestImg") || messageContent.equalsIgnoreCase("l")) {
                File image = new File(CameraEnvironment.getLATEST_FILE());
                InputStream inputStream;
                try {
                    inputStream = new FileInputStream(image);
                    FileUpload fileUpload = FileUpload.fromData(inputStream, "latest.png");
                    privateChannel.sendFiles(fileUpload).queue();
                } catch (FileNotFoundException e) {
                    privateChannel.sendMessage("No latest file right now!").queue();
                    e.printStackTrace();
                }
            }
            else if(messageContent.equalsIgnoreCase("!left") || messageContent.equalsIgnoreCase(("dl"))) {
                masterController.cameraMenu.menuMoveAction(-1);
                privateChannel.sendMessage(masterController.cameraMenu.getCurrentMainItem().getName().apply(null)).queue();
            }
            else if(messageContent.equalsIgnoreCase("!right") || messageContent.equalsIgnoreCase(("dr"))) {
                masterController.cameraMenu.menuMoveAction(1);
                privateChannel.sendMessage(masterController.cameraMenu.getCurrentMainItem().getName().apply(null)).queue();
            }
            else if(messageContent.equalsIgnoreCase("!current") ) {
                privateChannel.sendMessage(masterController.cameraMenu.getCurrentMainItem().getName().apply(null)).queue();
            }
            else if(messageContent.equalsIgnoreCase("!action") || messageContent.equalsIgnoreCase(("!click"))) {
                masterController.cameraMenu.menuTriggerCurrentAction();
                privateChannel.sendMessage(masterController.cameraMenu.getCurrentMainItem().getName().apply(null)).queue();
            }
            privateChannel.sendMessage("Message processed").queue();
        }).start();
    }

    private String screenshot() throws IOException, AWTException {
        String name = StringUtil.getCurrentTime();
        BufferedImage bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(bufferedImage, "png", new File(IMAGES_DIR+"/ss"+ name+".png"));
        return IMAGES_DIR+"/ss"+ name+".png";
    }
}
