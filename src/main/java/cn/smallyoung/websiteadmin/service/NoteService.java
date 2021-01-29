package cn.smallyoung.websiteadmin.service;

import cn.smallyoung.websiteadmin.base.BaseService;
import cn.smallyoung.websiteadmin.entity.Note;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author smallyoung
 * @data 2021/1/29
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class NoteService extends BaseService<Note, String> {


}
