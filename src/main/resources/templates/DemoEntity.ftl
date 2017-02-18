package ${entityPackageName};

import lombok.Data;
import org.framework.basic.entity.BaseEntity;
import javax.persistence.Table;
import ${importType}


@Data
@Table(name = "${tableName}")
public class ${entityName} extends BaseEntity {
    private ${javaType} ${javaField};
}
