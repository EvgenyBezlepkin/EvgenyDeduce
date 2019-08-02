package deduction.db.mappers;

import deduction.db.dto.RulesDTO;

import java.util.List;


public interface RulesMapper {

    int insertRulesDB(RulesDTO rulesDTO);

    List<RulesDTO> getRules(String name);

    void deleteRules(int ref_model);

}