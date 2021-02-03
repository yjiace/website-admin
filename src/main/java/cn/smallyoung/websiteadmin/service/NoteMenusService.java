package cn.smallyoung.websiteadmin.service;

import cn.hutool.core.util.StrUtil;
import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.dao.NoteMenusDao;
import cn.smallyoung.websiteadmin.entity.NoteMenus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author smallyoung
 * @data 2021/1/29
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class NoteMenusService extends BaseService<NoteMenus, String> {

    @Resource
    private NoteMenusDao noteMenusDao;

    public List<NoteMenus> findByUserId(String userId){
        return StrUtil.hasBlank(userId) ? null : noteMenusDao.findByUserIdOrderByCreateTimeDesc(userId);
    }

    public NoteMenus findByIdAndUserId(String id, String userId){
        return StrUtil.hasBlank(id, userId) ? null : noteMenusDao.findByIdAndUserIdAndIsDelete(id, userId, "N");
    }

}
