//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.12.29 at 09:17:16 上午 CST 
//

package sacred.alliance.magic.app.map.point;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 * <p>Java class for EventPoint complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventPoint">
 *   &lt;complexContent>
 *     &lt;extension base="{}Point">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventPoint")
/*@XmlSeeAlso( { SkillCollectPoint.class, JumpMapPoint.class,
		QuestCollectPoint.class })*/
public abstract class EventPoint<T extends AbstractRole> extends Point {

	public EventPoint(){
		
	}
	public EventPoint(int x, int y) {
		super("", x, y);
	}
	/**下面两个静态变量的值不可以更改**/
	public static final String TRUE = "" ;
	public static final String FALSE = "false" ;
	
	public abstract String isSatisfyCond(T role);

	public abstract void trigger(T role) throws ServiceException;

}
