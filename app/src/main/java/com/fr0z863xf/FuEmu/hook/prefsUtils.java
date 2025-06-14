package com.fr0z863xf.FuEmu.hook;

import android.content.SharedPreferences;
import android.os.Environment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//反序列化配置文件，由于sp的一些问题，最终选择这个方案来读取配置

public class prefsUtils implements SharedPreferences {

    private static final String DEFAULT_SUB_PATH = "FuEmu/FuEmuPrefs.json";
    private final File jsonFile;
    private JSONObject jsonData;

    // Default constructor uses a standard path in the Download directory
    public prefsUtils() {
        this(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), DEFAULT_SUB_PATH));
    }

    // Constructor allowing a custom file path
    public prefsUtils(File file) {
        this.jsonFile = file;
        loadPreferences();
    }

    private synchronized void loadPreferences() {
        if (jsonFile.exists() && jsonFile.isFile()) {
            if (jsonFile.length() == 0) { // Handle empty file explicitly
                this.jsonData = new JSONObject();
                return;
            }
            try (FileInputStream fis = new FileInputStream(jsonFile);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(chunk)) != -1) {
                    baos.write(chunk, 0, bytesRead);
                }
                String jsonString = baos.toString(StandardCharsets.UTF_8.name());
                if (jsonString.trim().isEmpty()) {
                    this.jsonData = new JSONObject();
                } else {
                    this.jsonData = new JSONObject(jsonString);
                }
            } catch (IOException | JSONException e) {
                System.err.println("Error loading preferences from " + jsonFile.getAbsolutePath() + ": " + e.getMessage());
                this.jsonData = new JSONObject(); // Initialize with empty JSON if loading fails
            }
        } else {
            // File doesn't exist or is a directory
            System.out.println("Preferences file not found or is a directory: " + jsonFile.getAbsolutePath() + ". Initializing with empty preferences.");
            this.jsonData = new JSONObject();
            File parentDir = jsonFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Could not create parent directory: " + parentDir.getAbsolutePath());
                }
            }
        }
    }

    private synchronized void savePreferences() {
        if (this.jsonData == null) {
            System.err.println("Attempted to save null jsonData. Initializing to empty.");
            this.jsonData = new JSONObject(); // Avoid NullPointerException if jsonData somehow became null
        }
        try {
            File parentDir = jsonFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Could not create directory for preferences file: " + parentDir.getAbsolutePath());
                    // If directory creation fails, FileOutputStream will likely fail.
                }
            }
            try (FileOutputStream fos = new FileOutputStream(jsonFile)) {
                fos.write(this.jsonData.toString(2).getBytes(StandardCharsets.UTF_8)); // Pretty print with indent 2
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error saving preferences to " + jsonFile.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String, Object> map = new HashMap<>();
        if (this.jsonData == null) return map;
        Iterator<String> keys = this.jsonData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object value = this.jsonData.get(key);
                map.put(key, value == JSONObject.NULL ? null : value);
            } catch (JSONException e) {
                System.err.println("Error getting value for key " + key + " in getAll: " + e.getMessage());
            }
        }
        return map;
    }

    @Override
    public String getString(String key, String defValue) {
        if (this.jsonData == null) return defValue;
        return this.jsonData.optString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        System.err.println("getStringSet is not supported in this implementation.");
        // throw new UnsupportedOperationException("getStringSet not supported");
        return defValues;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (this.jsonData == null) return defValue;
        return this.jsonData.optInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        if (this.jsonData == null) return defValue;
        return this.jsonData.optLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (this.jsonData == null) return defValue;
        return (float) this.jsonData.optDouble(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (this.jsonData == null) return defValue;
        return this.jsonData.optBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        if (this.jsonData == null) return false;
        return this.jsonData.has(key);
    }

    @Override
    public Editor edit() {
        return new JsonEditor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        System.err.println("registerOnSharedPreferenceChangeListener is not supported in this implementation.");
        // throw new UnsupportedOperationException("registerOnSharedPreferenceChangeListener not supported");
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        System.err.println("unregisterOnSharedPreferenceChangeListener is not supported in this implementation.");
        // throw new UnsupportedOperationException("unregisterOnSharedPreferenceChangeListener not supported");
    }

    private class JsonEditor implements Editor {
        private final JSONObject tempJsonChanges = new JSONObject();
        private boolean clearAll = false;

        @Override
        public Editor putString(String key, String value) {
            try {
                tempJsonChanges.put(key, value);
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.putString for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            System.err.println("putStringSet is not supported for key '" + key + "'.");
            // throw new UnsupportedOperationException("putStringSet not supported");
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            try {
                tempJsonChanges.put(key, value);
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.putInt for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            try {
                tempJsonChanges.put(key, value);
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.putLong for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            try {
                tempJsonChanges.put(key, (double) value); // Store as double in JSON
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.putFloat for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            try {
                tempJsonChanges.put(key, value);
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.putBoolean for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor remove(String key) {
            try {
                tempJsonChanges.put(key, JSONObject.NULL); // Mark for removal
            } catch (JSONException e) {
                System.err.println("Error in JsonEditor.remove for key '" + key + "': " + e.getMessage());
            }
            return this;
        }

        @Override
        public Editor clear() {
            this.clearAll = true;
            // Clear any pending changes in tempJsonChanges as well
            Iterator<String> keys = tempJsonChanges.keys();
            while(keys.hasNext()){
                keys.next();
                keys.remove();
            }
            return this;
        }

        @Override
        public boolean commit() {
            synchronized (prefsUtils.this) {
                if (prefsUtils.this.jsonData == null) {
                     // Should be initialized by loadPreferences, but as a safeguard:
                    prefsUtils.this.jsonData = new JSONObject();
                }

                if (clearAll) {
                    Iterator<String> keys = prefsUtils.this.jsonData.keys();
                    while (keys.hasNext()) {
                        keys.next();
                        keys.remove(); // Modifies prefsUtils.this.jsonData
                    }
                }

                Iterator<String> newKeys = tempJsonChanges.keys();
                while (newKeys.hasNext()) {
                    String key = newKeys.next();
                    try {
                        Object value = tempJsonChanges.get(key);
                        if (value == JSONObject.NULL) {
                            prefsUtils.this.jsonData.remove(key);
                        } else {
                            prefsUtils.this.jsonData.put(key, value);
                        }
                    } catch (JSONException e) {
                        System.err.println("Error committing key '" + key + "': " + e.getMessage());
                        // Continue with other changes
                    }
                }
                savePreferences(); // Persist the modified jsonData
            }
            // Reset editor state for this instance - though a new one is made per edit() call typically
            // tempJsonChanges = new JSONObject(); // Not strictly needed as new Editor is returned by edit()
            // clearAll = false;
            return true;
        }

        @Override
        public void apply() {
            // For this basic implementation, apply() is synchronous like commit().
            // A true asynchronous apply would involve a background thread.
            commit();
        }
    }
}
