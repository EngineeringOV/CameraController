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
import ventures.of.util.EnvironmentVariableUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//todo set settings through discord
//todo desktop screenshot
@Slf4j
public class DiscordController extends ListenerAdapter {
    private final MasterController masterController;
    public DiscordController(MasterController masterController) {
        this.masterController = masterController;
        JDABuilder.createDefault(EnvironmentVariableUtil.getPropertyString("discord.api.token"))
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
            if (event.getAuthor().isBot() /* && event.getAuthor().getName().equals() */) {
                return;
            }
            PrivateChannel privateChannel = message.getChannel().asPrivateChannel();
            String messageContent = event.getMessage().getContentRaw();
            privateChannel.sendMessage("Message received, processing").queue();
            if (messageContent.equalsIgnoreCase("!help")) {
                String str = "Here is the current commands\n" + "!killCam / kc\n" +
                        "!settings\n" +
                        "!snap / s\n" +
                        "!timelapse / tl\n" +
                        "!video / v\n" +
                        "!latestImg / l\n";
                privateChannel.sendMessage(str).queue();
            }
            if (messageContent.equalsIgnoreCase("!settings")) {

                privateChannel.sendMessage("ShutterTime = " + masterController.cameraController.getShutterTime().getActualValue()).queue();
                privateChannel.sendMessage("Gain = " + masterController.cameraController.getGain().getActualValue()).queue();
                privateChannel.sendMessage("Time between images (tl) = " + masterController.cameraController.getTlTimeBetween().getActualValue()).queue();
            }
            if (messageContent.equalsIgnoreCase("!killCam") || messageContent.equalsIgnoreCase("kc")) {
                CameraController.killLibCamera();
                privateChannel.sendMessage("It's dead").queue();
            }
            if (messageContent.equalsIgnoreCase("!snap") || messageContent.equalsIgnoreCase("s")) {
                masterController.cameraController.triggerTakeStill(8000,false);
                privateChannel.sendMessage("SNAP!").queue();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (messageContent.equalsIgnoreCase("!timelapse") || messageContent.equalsIgnoreCase("tl")) {
                CameraController.killLibCamera();
                masterController.cameraController.triggerTimelapse();
                privateChannel.sendMessage("Timelapse is going!").queue();
            }
            if (messageContent.equalsIgnoreCase("!video") || messageContent.equalsIgnoreCase("v")) {
                CameraController.killLibCamera();
                masterController.cameraController.triggerVideo();
                privateChannel.sendMessage("Video is going!").queue();
            }
            if (messageContent.equalsIgnoreCase("!latestImg") || messageContent.equalsIgnoreCase("l")
                    || messageContent.equalsIgnoreCase("!snap") || messageContent.equalsIgnoreCase("s")) {
                File image = new File(CameraController.getLATEST_FILE());
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
            privateChannel.sendMessage("Message processed").queue();
        }).start();
    }
}
