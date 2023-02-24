package cc.mewcraft.mewutils.api.module;

public interface ModuleIdentifier {

    /**
     * Returns the id of this module, which is the direct package name of the runtime class.
     *
     * @return the direct package name of the runtime class
     */
    default String getId() {
        String packageName = getClass().getPackage().getName();
        String directParent;
        if (packageName.contains(".")) {
            directParent = packageName.substring(1 + packageName.lastIndexOf("."));
        } else {
            directParent = packageName;
        }
        return directParent;
    }

}
