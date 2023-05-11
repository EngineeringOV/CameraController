package ventures.of.controller;

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
import ventures.of.MainProgram;
import ventures.of.util.EnvironmentVariableUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

//todo clean this S%%Tuff up
//todo set settings through discord
//todo desktop screenshot
public class DiscordController extends ListenerAdapter {
    public DiscordController() {
        JDABuilder.createDefault(EnvironmentVariableUtil.getProperty("discord.api.token"))
                .addEventListeners(this)
                .setActivity(Activity.playing("Commands = !help"))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Discord bot is ready!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
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
            privateChannel.sendMessage("Message recieved, processing").queue();


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

                privateChannel.sendMessage("ShutterTime = " + MainProgram.cameraController.getShutterTime().getActualValue()).queue();
                privateChannel.sendMessage("Gain = " + MainProgram.cameraController.getGain().getActualValue()).queue();
                privateChannel.sendMessage("Time between images (tl) = " + MainProgram.cameraController.getTlTimeBetween().getActualValue()).queue();
            }
            if (messageContent.equalsIgnoreCase("!killCam") || messageContent.equalsIgnoreCase("kc")) {
                MainProgram.cameraController.killLibCamera();
                privateChannel.sendMessage("It's dead").queue();
            }
            if (messageContent.equalsIgnoreCase("!snap") || messageContent.equalsIgnoreCase("s")) {

                MainProgram.cameraController.triggerTakeStill();

                privateChannel.sendMessage("SNAP!").queue();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (messageContent.equalsIgnoreCase("!timelapse") || messageContent.equalsIgnoreCase("tl")) {
                MainProgram.cameraController.killLibCamera();
                MainProgram.cameraController.triggerTimelapse();
                privateChannel.sendMessage("Timelapse is going!").queue();
            }
            if (messageContent.equalsIgnoreCase("!video") || messageContent.equalsIgnoreCase("v")) {
                MainProgram.cameraController.killLibCamera();
                MainProgram.cameraController.triggerVideo();
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
