package ${rootPackageName};
import org.gof.core.gen.GofGenFile;
import com.google.protobuf.CodedInputStream;
import org.gof.core.support.SysException;
import org.gof.core.InputStream;

@GofGenFile
public final class ${rootClassName}{
	public static ${interfaceName} create(int id, CodedInputStream s){
		try{
			switch(id){
			<#list methodsList as m>
			case ${m.id}:
				return ${m.className}.parseFrom(s);
			</#list>
			}
		}catch (Exception e) {
			throw new SysException(e);
		}
		return null;
	}
	public static void init(){
		InputStream.setCreateMsgFunc(${rootClassName}::create);
	}
}
