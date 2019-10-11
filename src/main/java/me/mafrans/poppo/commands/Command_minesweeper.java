package me.mafrans.poppo.commands;

import me.mafrans.poppo.commands.util.Command;
import me.mafrans.poppo.commands.util.CommandCategory;
import me.mafrans.poppo.commands.util.CommandMeta;
import me.mafrans.poppo.commands.util.ICommand;
import me.mafrans.poppo.util.GUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Random;

public class Command_minesweeper implements ICommand {
    @Override
    public String getName() {
        return "minesweeper";
    }

    @Override
    public CommandMeta getMeta() {
        return new CommandMeta(CommandCategory.FUN, "Play a game of minesweeper.", "minesweeper [width] [height] [mines]", new String[] {"mines", "sweep", "mine", "sweeper"}, false);
    }

    @Override
    public boolean onCommand(Command command, TextChannel channel) throws Exception {
        String[] args = command.getArgs();
        int width = 10;
        int height = 10;

        StringBuilder errorBuilder = new StringBuilder();
        if(args.length > 0) {
            if(NumberUtils.isParsable(args[0])) {
                int newWidth = Integer.parseInt(args[0]);
                if (newWidth < 5) {
                    errorBuilder.append("Width cannot be less than 14").append("\n");
                }
                else if (newWidth > 15) {
                    errorBuilder.append("Width cannot be more than 14").append("\n");
                }
                else {
                    width = newWidth;
                }
            }
            else {
                errorBuilder.append(args[0] + " is not an integer.").append("\n");
            }
        }

        if(args.length > 1) {
            if(NumberUtils.isParsable(args[1])) {
                int newHeight = Integer.parseInt(args[1]);
                if (newHeight < 5) {
                    errorBuilder.append("Height cannot be less than 5").append("\n");
                }
                else if (newHeight > 15) {
                    errorBuilder.append("Height cannot be more than 15").append("\n");
                }
                else {
                    height = newHeight;
                }
            }
            else {
                errorBuilder.append(args[1] + " is not an integer.").append("\n");
            }
        }

        int mines = (int) Math.floor(((width*height)/256f)*50);

        if(args.length > 2) {
            if(NumberUtils.isParsable(args[2])) {
                int newMines = Integer.parseInt(args[2]);
                if (newMines < 1) {
                    errorBuilder.append("There cannot be fewer than 1 mine.").append("\n");
                }
                else if (newMines > width*height-1) {
                    errorBuilder.append("There cannot be more than " + (width*height-1) + " mines.").append("\n");
                }
                else {
                    mines = newMines;
                }
            }
            else {
                errorBuilder.append(args[2] + " is not an integer.").append("\n");
            }
        }

        String[][] mineField = generateMinefield(width, height, mines);
        StringBuilder emotes = new StringBuilder();

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                emotes.append("||");
                if (NumberUtils.isParsable(mineField[x][y])) {
                    emotes.append(GUtil.getNumberEmote(Integer.parseInt(mineField[x][y])));
                } else {
                    emotes.append(":").append(mineField[x][y]).append(":");
                }
                emotes.append("||");
                System.out.println(x + ", " + y);
            }
            emotes.append("\n");
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(GUtil.randomColor());
        embedBuilder.setAuthor("Minesweeper " + width + "x" + height + ", " + mines + " mines", null, "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/twitter/180/bomb_1f4a3.png");
        embedBuilder.setDescription(emotes.toString());
        if(!errorBuilder.toString().isEmpty()) {
            embedBuilder.addField("Errors", errorBuilder.toString(), false);
        }

        channel.sendMessage(embedBuilder.build()).queue();

        return true;
    }


    private String[][] generateMinefield(int width, int height, int mines) {
        String[][] out = new String[width][height];

        Random random = new Random();
        for(int i = 0; i < mines; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if(out[x][y] != null && out[x][y].equals("bomb")) {
                i--;
                continue;
            }
            out[x][y] = "bomb";
        }

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(out[x][y] != null && out[x][y].equals("bomb")) continue;

                int bombs = 0;
                if(out[x].length >= y+2) {
                    if(out[x][y+1] != null && out[x][y+1].equals("bomb")) bombs++;
                }
                if(out.length >= x+2 && out[x+1].length >= y+2) {
                    if(out[x+1][y+1] != null && out[x+1][y+1].equals("bomb")) bombs++;
                }
                if(out.length >= x+2) {
                    if(out[x+1][y] != null && out[x+1][y].equals("bomb")) bombs++;
                }
                if(out.length >= x+2 && y - 1 >= 0) {
                    if(out[x+1][y-1] != null && out[x+1][y-1].equals("bomb")) bombs++;
                }
                if(y - 1 >= 0) {
                    if(out[x][y-1] != null && out[x][y-1].equals("bomb")) bombs++;
                }
                if(x - 1 >= 0 && y - 1 >= 0) {
                    if(out[x-1][y-1] != null && out[x-1][y-1].equals("bomb")) bombs++;
                }
                if(x - 1 >= 0) {
                    if(out[x-1][y] != null && out[x-1][y].equals("bomb")) bombs++;
                }
                if(x - 1 >= 0 && out[x].length >= y+2) {
                    if(out[x-1][y+1] != null && out[x-1][y+1].equals("bomb")) bombs++;
                }

                out[x][y] = String.valueOf(bombs);
            }
        }

        return out;
    }
}
