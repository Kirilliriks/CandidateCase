package me.kirillirik.candidate;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class SignBase {

    private static final String BASE_FILE = "singbase.txt";

    private final Set<String> signs = new HashSet<>();

    public void addSign(String sign) {
        signs.add(sign);
    }

    public void addIfNeedSigns(Rule rule) {
        addIfNeedSigns(rule.getRoot());
    }

    private void addIfNeedSigns(Part part) {
        if (part.isSign()) {
            addSign(part.getTitle().toLowerCase());
        }

        for (final Part child : part.getChildren()) {
            addIfNeedSigns(child);
        }
    }

    public boolean contains(String sign) {
        return signs.contains(sign);
    }

    public void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_FILE))) {
            for (final String sign : signs) {
                writer.write(sign);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        final File file = new File(BASE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(BASE_FILE))) {
            String line = reader.readLine();

            while (line != null) {
                addSign(line);

                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
