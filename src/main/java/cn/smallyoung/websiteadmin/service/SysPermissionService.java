package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.SysPermissionDao;
import cn.smallyoung.websiteadmin.entity.SysPermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smallyoung
 * @date 2020/7/26
 */

@Service
@Transactional(readOnly = true)
public class SysPermissionService extends BaseService<SysPermission, String> {

    @Resource
    private SysPermissionDao sysPermissionDao;

    /**
     * 根据id集合查询所有权限
     *
     * @param idList 权限id列表
     * @return 权限对象列表
     */
    public List<SysPermission> findByIdInAndIsDelete(List<String> idList) {
        return sysPermissionDao.findByIdInAndIsDelete(idList, "N");
    }

    public List<Tree<String>> toTree(List<SysPermission> sysPermissions){
        if(CollectionUtil.isEmpty(sysPermissions)){
            return new ArrayList<>();
        }
        return TreeUtil.build(sysPermissions, "0", new TreeNodeConfig(),
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setName(treeNode.getName());
                    tree.putExtra("val", treeNode.getVal());
                    tree.putExtra("jumpPath", treeNode.getJumpPath());
                    tree.putExtra("requestPath", treeNode.getRequestPath());
                    tree.putExtra("icon", treeNode.getIcon());
                    tree.putExtra("orderNumber", treeNode.getOrderNumber());
                    tree.putExtra("type", treeNode.getType());
                    tree.putExtra("updateTime", treeNode.getUpdateTime());
                });
    }
}
