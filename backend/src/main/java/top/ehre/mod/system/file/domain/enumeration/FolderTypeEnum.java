package top.ehre.mod.system.file.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LibrhHp_0928
 * @since 2025-11-24 18:58:32
 */
@Getter
@AllArgsConstructor
public enum FolderTypeEnum {
    AVATAR((byte) 1, "avatar"),
    COMMON((byte) 2, "common");

    private final Byte value;

    private final String desc;

    public static FolderTypeEnum getByValue(Byte value) {
        for (FolderTypeEnum folderType : FolderTypeEnum.values()) {
            if (folderType.getValue().equals(value)) {
                return folderType;
            }
        }
        return null;
    }
}
