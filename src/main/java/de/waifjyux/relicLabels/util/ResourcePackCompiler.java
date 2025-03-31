package de.waifjyux.relicLabels.util;

import com.google.gson.JsonArray;
import de.waifjyux.relicLabels.RelicLabels;
import de.waifjyux.relicLabels.labels.RelicTier;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackCompiler {

    private ResourcePackCompiler() {
    }

    public static void compile() throws IOException {

        createUnicodeJSON();
        createImages();
        zipPack();

    }

    private static void createUnicodeJSON() throws IOException {

        int baseUnicode = BaseConfig.baseUnicode;

        File unicodeFile = new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack/assets/minecraft/font/default.json");

        if(!unicodeFile.getParentFile().exists()) unicodeFile.getParentFile().mkdirs();
        if(!unicodeFile.exists()) unicodeFile.createNewFile();

        JSONObject unicodes = new JSONObject();
        JSONArray providers = new JSONArray();

        providers.add(createUnicode("reset.png", -1000, -6, baseUnicode));
        providers.add(createUnicode("reset.png", -1000, -3, baseUnicode + 1));
        providers.add(createUnicode("reset.png", -1000, -34, baseUnicode + 2));
        providers.add(createUnicode("reset.png", -1000, -9, baseUnicode + 3));

        int itter = 0;
        for (RelicTier tier : RelicTier.getTiers()) {
            providers.add(createUnicode(tier.getName() + "/topleftcorner.png", 23, 23, baseUnicode + 4 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/topbar.png", 23, 23, baseUnicode + 5 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/toprightcorner.png", 23, 23, baseUnicode + 6 + itter * 12));

            providers.add(createUnicode(tier.getName() + "/titleplaquestart.png", 14, 35, baseUnicode + 7 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/titleplaque.png", 14, 35, baseUnicode + 8 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/titleplaqueend.png", 14, 35, baseUnicode + 9 + itter * 12));

            providers.add(createUnicode(tier.getName() + "/lineborder.png", 9, 10, baseUnicode + 10 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/line.png", 9, 10, baseUnicode + 11 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/linestrike.png", 9, 10, baseUnicode + 12 + itter * 12));

            providers.add(createUnicode(tier.getName() + "/bottomleftcorner.png", 9, 10, baseUnicode + 13 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/bottombar.png", 9, 10, baseUnicode + 14 + itter * 12));
            providers.add(createUnicode(tier.getName() + "/bottomrightcorner.png", 9, 10, baseUnicode + 15 + itter * 12));

            itter++;
        }

        unicodes.put("providers", providers);

        String json = unicodes.toJSONString().replace("\\/", "/");
        Files.write(unicodeFile.toPath(), json.getBytes());

    }

    private static JSONObject createUnicode(String path, int ascent, int height, int unicodeChar) {
        JSONObject unicode = new JSONObject();
        unicode.put("file", "minecraft:font/relic/" + path);
        unicode.put("type", "bitmap");
        unicode.put("ascent", ascent);
        unicode.put("height", height);
        JsonArray chars = new JsonArray();
        chars.add((char) unicodeChar);
        unicode.put("chars", chars);
        return unicode;
    }

    private static void createImages() throws IOException {

        for(RelicTier tier : RelicTier.getTiers()) {
            createImage(tier, "titleplaque");
            createImage(tier, "topleftcorner");
            createImage(tier, "topbar");
            createImage(tier, "toprightcorner");
            createImage(tier, "titleplaquestart");
            createImage(tier, "titleplaqueend");
            createImage(tier, "lineborder");
            createImage(tier, "line");
            createImage(tier, "linestrike");
            createImage(tier, "bottomleftcorner");
            createImage(tier, "bottombar");
            createImage(tier, "bottomrightcorner");
        }

        Files.copy(ResourcePackCompiler.class.getClassLoader().getResourceAsStream("img/reset.png"),
                new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack/assets/minecraft/textures/font/relic/reset.png").toPath());
        Files.copy(ResourcePackCompiler.class.getClassLoader().getResourceAsStream("pack.mcmeta"),
                new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack/pack.mcmeta").toPath());
        Files.copy(ResourcePackCompiler.class.getClassLoader().getResourceAsStream("pack.png"),
                new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack/pack.png").toPath());

    }

    //main color 255 255 255
    //second color 181 181 181
    //third color 104 104 104

    private static void createImage(RelicTier tier, String name) throws IOException {

        File file = new File(RelicLabels.getPlugin().getDataFolder() + "/resources", name + ".png");
        BufferedImage img = ImageIO.read(file);

        BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = convertedImg.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        img = convertedImg;

        Color replacePrimary = new Color(255, 255, 255);
        Color replaceSecondary = new Color(181, 181, 181);
        Color replaceTertiary = new Color(104, 104, 104);

        Color primary = tier.getPrimaryColor();
        Color secondary = tier.getSecondaryColor();
        Color tertiary = tier.getThertiaryColor();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                if (rgb == replacePrimary.getRGB()) {
                    img.setRGB(x, y, primary.getRGB());
                }
                else if (rgb == replaceSecondary.getRGB()) {
                    img.setRGB(x, y, secondary.getRGB());
                }
                else if (rgb == replaceTertiary.getRGB()) {
                    img.setRGB(x, y, tertiary.getRGB());
                }
            }
        }

        File out = new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack/assets/minecraft/textures/font/relic/" + tier.getName() + "/" + name + ".png");
        if(!out.getParentFile().exists()) out.getParentFile().mkdirs();
        if(!out.exists()) out.createNewFile();

        ImageIO.write(img, "png", out);

    }

    private static void zipPack() throws IOException {
        File packFolder = new File(RelicLabels.getPlugin().getDataFolder(), "resourcepack");
        File zipFile = new File(RelicLabels.getPlugin().getDataFolder(), "RelicLabels-v1.0.0.zip");

        if (zipFile.exists()) zipFile.delete();

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolderContents(packFolder, zos, "");
        }

        deleteFolder(packFolder);
    }

    private static void zipFolderContents(File folder, ZipOutputStream zos, String baseName) throws IOException {
        for (File file : folder.listFiles()) {
            String zipEntryName = baseName.isEmpty() ? file.getName() : baseName + "/" + file.getName();

            if (file.isDirectory()) {
                zipFolderContents(file, zos, zipEntryName);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(zipEntryName));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
        }
    }





    private static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        folder.delete();
    }


}
