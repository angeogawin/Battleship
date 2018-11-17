package tp7_api;


/**
 * 
 * @author pfister - connecthive.com
 * initial api and implementation
 *
 */
public interface IClientView extends IBaseView {
	void setClientControler(IClientControler clientControler);
	void setId(int[] id);
}
