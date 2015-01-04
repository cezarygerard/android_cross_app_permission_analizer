package com.cgz.capa.services;

import com.cgz.capa.model.*;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by czarek on 04/01/15.
 */
@Component
public class SystemPermissionsInfoService {

    private static final String ANDROID_NAMESPACE_URI = "http://schemas.android.com/apk/res/android";
    private static final String PERMISSION_TAG_NAME="permission";
    private static final String PERMISSION_GROUP_TAG_NAME="permission-group";

    private String coreManifestUri;

    private Map<String, Permission> permissions;
    private Map<String, PermissionGroup> permissionGroups;

    public SystemPermissionsInfoService(String coreManifestUri){
        this.coreManifestUri = coreManifestUri;
    }

    public void readCoreManifest(){
        URL url = null;
        try {
            url = new URL(coreManifestUri);
            URLConnection conn = url.openConnection();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document manifestXmlDoc = builder.parse(conn.getInputStream());
            parseManifest(manifestXmlDoc);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


    }

    private void parseManifest(Document manifestXmlDoc) {
        permissions = new Hashtable<String,Permission>();
        permissionGroups = new Hashtable<String,PermissionGroup>();
        parsePermissionGroupsNodeList(manifestXmlDoc.getElementsByTagName(PERMISSION_GROUP_TAG_NAME));
        //sequence is important -parse groups first,
        parsePermissionsNodeList(manifestXmlDoc.getElementsByTagName(PERMISSION_TAG_NAME));

    }

    private void parsePermissionGroupsNodeList(NodeList permissionsGroupsNodes) {
        for (int i = 0; i < permissionsGroupsNodes.getLength(); i++) {
            Node node =  permissionsGroupsNodes.item(i);
            Element element = (Element) node;

            NamedNodeMap attributes = element.getAttributes();

            String permissionGroupName =  attributes.getNamedItem("android:name").getNodeValue();
            int priority = 0 ;
            Node priorityNode =  attributes.getNamedItem("android:priority");
            if(priorityNode!=null){
                priority = Integer.parseInt(priorityNode.getNodeValue());
            }

            PermissionGroupFlag permissionGroupFlag = decodePermissionGroupFlagString(attributes.getNamedItem("android:permissionGroupFlags"));
            permissionGroups.put(permissionGroupName, new PermissionGroup(permissionGroupName, permissionGroupFlag, priority));
        }
    }



    private PermissionGroupFlag decodePermissionGroupFlagString(Node permissionGroupFlagAttr) {
        if(permissionGroupFlagAttr!= null) {
            String permissionGroupFlagString = permissionGroupFlagAttr.getNodeValue();
            PermissionGroupFlag.valueOf(permissionGroupFlagString);
            //TODO zamienic na valueof
            if (PermissionGroupFlag.PERSONAL_INFO.getFlagName().equals(permissionGroupFlagString)) {
                return PermissionGroupFlag.PERSONAL_INFO;
            }
        }
        return PermissionGroupFlag.NONE;
    }

    private void parsePermissionsNodeList(NodeList permissionsNodes) {


        for (int i = 0; i < permissionsNodes.getLength(); i++) {
            Node node =  permissionsNodes.item(i);
            Element element = (Element) node;

            NamedNodeMap attributes = element.getAttributes();

            String permissionName =  attributes.getNamedItem("android:name").getNodeValue();
            PermissionGroup group = decodePermissionGroupFromPermissionNode(attributes.getNamedItem("android:permissionGroup"));
            ProtectionLevel protectionLevel = decodeProtectionLevelFromPermissionNode(attributes.getNamedItem("android:protectionLevel"));
            PermissionFlag flag = decodeFlagFromPermissionNode(attributes.getNamedItem("android:permissionFlags"));

            permissions.put(permissionName, new Permission(permissionName, group, protectionLevel,flag ));
        }
    }

    private PermissionFlag decodeFlagFromPermissionNode(Node namedItem) {
        return null;
    }

    private ProtectionLevel decodeProtectionLevelFromPermissionNode(Node protectionLevelAttr) {
        return null;
    }


    private PermissionGroup decodePermissionGroupFromPermissionNode(Node permissionGroupAttr) {
        if(permissionGroupAttr != null) {
            String groupName = permissionGroupAttr.getNodeValue();
            if(groupName != null) {
                return permissionGroups.get(groupName);
            }
        }
        return null;
    }



    public Map<String, PermissionGroup> getPermissionGroups() {
        if (permissionGroups == null){
            throw new IllegalStateException("nothing was read yet");
        }

        return permissionGroups;

    }
}
