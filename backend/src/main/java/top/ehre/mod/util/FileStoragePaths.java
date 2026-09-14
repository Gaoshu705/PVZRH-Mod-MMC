package top.ehre.mod.util;

import java.nio.file.Path;

/**
 * 统一文件存储路径，避免上传目录和 /static 映射不一致。
 */
public final class FileStoragePaths {

    private FileStoragePaths() {
    }

    public static Path root(String localPath) {
        String normalized = localPath == null ? "" : localPath.trim().replace('\\', '/');
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("file.localPath 不能为空");
        }
        return Path.of(normalized).toAbsolutePath().normalize();
    }

    public static Path folder(String localPath, String folderName) {
        return root(localPath).resolve(folderName);
    }

    public static String resourceLocation(String localPath) {
        String uri = root(localPath).toUri().toString();
        if (!uri.endsWith("/")) {
            uri += "/";
        }
        return uri;
    }
}
