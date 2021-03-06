package com.zdtech.platform.service.simulator;

import com.zdtech.platform.framework.entity.SimSystem;
import com.zdtech.platform.framework.repository.SimSystemDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by htma on 2016/5/9.
 */
@Service
public class SimSystemService {
    @Autowired
    private SimSystemDao simSystemDao;

    public SimSystem get(Long id) {
        return simSystemDao.findOne(id);
    }
    public Map<String,Object> getAll() {
        List<SimSystem> list = simSystemDao.findAll();
        Map<String,Object> ret = new HashMap<>();
        if (list == null || list.size() < 1){
            ret.put("total",0);
            ret.put("rows",list);
            return ret;
        }
        ret.put("total",list.size());
        ret.put("rows",list);
        return ret;
    }
    public void addSim(SimSystem simSystem) {
        simSystemDao.save(simSystem);
    }
    public void deleteSims(List<Long> list) {
        for (Long id:list){
            deleteSimSet(id);
        }
    }
    public void deleteSimSet(Long id){
        simSystemDao.delete(id);
    }

    public List<SimSystem> getallsim(String type) {
        List<SimSystem> lists =simSystemDao.fingByType(type);
        return lists;
    }

    public List<Map<String,String>> getAllSimSystemOrder(String type) {
        List<Map<String, String>> ret = new ArrayList<>();
        List<Object[]> rows = null;
        if (StringUtils.isEmpty(type)){
            rows = simSystemDao.findSimSystemOrder();
        }else {
            rows = simSystemDao.findSimSystemOrder(type);
        }
        if (rows == null){
            return ret;
        }
        for (Object[] row : rows) {
            Map<String,String> map = new HashMap<>();
            map.put("id",row[0].toString());
            map.put("text", row[1].toString());
            map.put("protocol",row[2].toString());
            ret.add(map);
        }
        return ret;
    }
}