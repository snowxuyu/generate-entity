package ${entityPackageName};

import lombok.Data;
import com.mobanker.framework.entity.BaseEntity;
import javax.persistence.Table;
${requeireImport}


@Data
@Table(name = "${tableName}")
public class ${entityName} extends BaseEntity {
${javaTypeField}
}
