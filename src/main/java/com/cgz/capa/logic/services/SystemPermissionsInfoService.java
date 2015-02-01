package com.cgz.capa.logic.services;

import com.cgz.capa.exceptions.ServiceException;
import com.cgz.capa.model.Permission;
import com.cgz.capa.model.PermissionGroup;
import com.cgz.capa.model.enums.PermissionFlag;
import com.cgz.capa.model.enums.PermissionGroupFlag;
import com.cgz.capa.model.enums.ProtectionLevel;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
public class SystemPermissionsInfoService {

    //TODO take into account namespaces and its prefixes!

    private static final String PERMISSION_TAG_NAME = "permission";
    private static final String PERMISSION_GROUP_TAG_NAME = "permission-group";
    private static final String ANDROID_NAME_ATTR_NAME = "android:name";
    private static final String PRIORITY_ATTR_NAME = "android:priority";
    private static final String PERMISSION_GROUP_FLAGS_ATTR_NAME = "android:permissionGroupFlags";
    private static final String PERMISSION_GROUP_ATTR_NAME = "android:permissionGroup";
    private static final String PROTECTION_LEVEL_ATTR_NAME = "android:protectionLevel";
    private static final String PERMISSION_FLAGS_ATTR_NAME = "android:permissionFlags";


    private String coreManifestUri;

    private Map<String, Permission> permissionsMap;
    private Map<String, PermissionGroup> permissionGroupsMap;

    public SystemPermissionsInfoService(String coreManifestUri) {
        this.coreManifestUri = coreManifestUri;
    }

    @PostConstruct
    private void readCoreManifest() throws ServiceException {
        InputStream is = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            is = getClass().getClassLoader().getResourceAsStream(coreManifestUri);
            Document manifestXmlDoc = builder.parse(is);
            parseManifest(manifestXmlDoc);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new ServiceException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new ServiceException("Exception at closing stream after another exception. I will go kill myself", e);
                }
            }
        }
    }

    private void parseManifest(Document manifestXmlDoc) {
        permissionsMap = new LinkedHashMap<String, Permission>();
        permissionGroupsMap = new LinkedHashMap<String, PermissionGroup>();
        parsePermissionGroupsNodeList(manifestXmlDoc.getElementsByTagName(PERMISSION_GROUP_TAG_NAME));
        //sequence is important -parse groups first,
        parsePermissionsNodeList(manifestXmlDoc.getElementsByTagName(PERMISSION_TAG_NAME));

    }

    private void parsePermissionGroupsNodeList(NodeList permissionsGroupsNodes) {
        for (int i = 0; i < permissionsGroupsNodes.getLength(); i++) {
            Node node = permissionsGroupsNodes.item(i);
            Element element = (Element) node;

            NamedNodeMap attributes = element.getAttributes();

            String permissionGroupName = attributes.getNamedItem(ANDROID_NAME_ATTR_NAME).getNodeValue();
            int priority = 0;
            Node priorityNode = attributes.getNamedItem(PRIORITY_ATTR_NAME);
            if (priorityNode != null) {
                priority = Integer.parseInt(priorityNode.getNodeValue());
            }

            PermissionGroupFlag permissionGroupFlag = decodePermissionGroupFlagString(attributes.getNamedItem(PERMISSION_GROUP_FLAGS_ATTR_NAME));
            permissionGroupsMap.put(permissionGroupName, new PermissionGroup(permissionGroupName, permissionGroupFlag, priority));
        }
    }

    private void parsePermissionsNodeList(NodeList permissionsNodes) {

        for (int i = 0; i < permissionsNodes.getLength(); i++) {
            Node node = permissionsNodes.item(i);
            Element element = (Element) node;

            NamedNodeMap attributes = element.getAttributes();

            String permissionName = attributes.getNamedItem(ANDROID_NAME_ATTR_NAME).getNodeValue();
            PermissionGroup group = decodePermissionGroupFromPermissionNode(attributes.getNamedItem(PERMISSION_GROUP_ATTR_NAME));
            ProtectionLevel protectionLevel = decodeProtectionLevelFromPermissionNode(attributes.getNamedItem(PROTECTION_LEVEL_ATTR_NAME));
            PermissionFlag flag = decodeFlagFromPermissionNode(attributes.getNamedItem(PERMISSION_FLAGS_ATTR_NAME));

            permissionsMap.put(permissionName, new Permission(permissionName, group, protectionLevel, flag));
        }
    }

    private PermissionGroupFlag decodePermissionGroupFlagString(Node permissionGroupFlagAttr) {
        if (permissionGroupFlagAttr != null) {
            String permissionGroupFlagString = permissionGroupFlagAttr.getNodeValue();
            return PermissionGroupFlag.getEnumValueByName(permissionGroupFlagString);
        }
        return null;
    }

    private PermissionFlag decodeFlagFromPermissionNode(Node permissionFlagAttr) {
        if (permissionFlagAttr != null) {
            return PermissionFlag.getEnumValueByName(permissionFlagAttr.getNodeValue());
        }
        return null;
    }

    private ProtectionLevel decodeProtectionLevelFromPermissionNode(Node protectionLevelAttr) {
        if (protectionLevelAttr == null) {
            return ProtectionLevel.NORMAL;
        }
        return ProtectionLevel.getEnumValueByName(protectionLevelAttr.getNodeValue());
    }


    private PermissionGroup decodePermissionGroupFromPermissionNode(Node permissionGroupAttr) {
        if (permissionGroupAttr != null) {
            String groupName = permissionGroupAttr.getNodeValue();
            if (groupName != null) {
                return permissionGroupsMap.get(groupName);
            }
        }
        return null;
    }


    public Map<String, PermissionGroup> getPermissionGroupsMap() {
        if (permissionGroupsMap == null) {
            throw new IllegalStateException("nothing was read yet");
        }
        return permissionGroupsMap;
    }

    public Map<String, Permission> getPermissionsMap() {
        if (permissionsMap == null) {
            throw new IllegalStateException("nothing was read yet");
        }
        return permissionsMap;
    }

    public Permission getPermission(String permissionName) {
        return permissionsMap.get(permissionName);
    }

    public PermissionGroup getPermissionsGroup(String permissionGroupName) {
        return permissionGroupsMap.get(permissionGroupName);
    }

    public boolean usesPersonalInfo(Permission permission) {
        if (permission.getGroup() == null) {
            return false;
        }
        return PermissionGroupFlag.PERSONAL_INFO.equals(permission.getGroup().getFlag());
    }

    public boolean costsMoney(Permission permission) {
        return PermissionFlag.COSTS_MONEY.equals(permission.getFlag());
    }
}
