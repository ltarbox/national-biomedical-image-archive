/**
* $Id$
*
* $Log: not supported by cvs2svn $
* Revision 1.2  2007/10/12 16:21:57  panq
* Removed unused code.
*
*/
package gov.nih.nci.ncia.markup;

import gov.nih.nci.ncia.dao.AbstractDAO;
import gov.nih.nci.ncia.dto.MarkupDTO;
import gov.nih.nci.ncia.internaldomain.GeneralSeries;
import gov.nih.nci.ncia.internaldomain.ImageMarkup;

import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class MarkupManagerImpl extends AbstractDAO
                               implements MarkupManager {
    private static Logger logger = Logger.getLogger(MarkupManagerImpl.class);

    protected MarkupManagerImpl() {}
    
    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#getMarkups(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)
    public String getMarkups(MarkupDTO dto) throws DataAccessException {
        List<String> results = null;
        String hql = "select imgmkup.markupContent from ImageMarkup imgmkup where ";
        hql = hql + "imgmkup.seriesInstanceUID = '" + dto.getSeriesUID() + "'";
        String data = null;


    	results = getHibernateTemplate().find(hql);

        if (results != null && results.size() > 0) {
            data = results.get(0);
        }

        return data;
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#saveMarkup(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)	
    public void saveMarkup(MarkupDTO dto) throws DataAccessException {
        if (isDuplicate(dto)) {
            updateMarkup(dto);
        }
        else {
            insertMarkup(dto);
        }
     }

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#updateMarkup(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)	
    public void updateMarkup(MarkupDTO dto) throws DataAccessException {
        String hql = "update ImageMarkup set markupContent = ?, submissionDate = ? "+
                     "where seriesInstanceUID = ?";
        
        
        getHibernateTemplate().bulkUpdate(hql, 
        		                          new Object[] {
        		                              dto.getMarkupData(), 
        		                              dto.getSeriesUID(), 
        		                              new java.util.Date()
       		                              });
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#insertMarkup(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)	
    public void insertMarkup(MarkupDTO dto) throws DataAccessException {
        Session session = null;
        ImageMarkup im = new ImageMarkup();
        GeneralSeries series = new GeneralSeries();
        String hql = "from GeneralSeries as series where ";
        hql += (" series.seriesInstanceUID = '" +
        dto.getSeriesUID().trim() + "' ");
        List retList = getHibernateTemplate().find(hql);
        if (retList != null && retList.size() >0) {
            series = (GeneralSeries) retList.get(0);
        }

        if (series == null) {
            logger.error("!!!!can not get series pk id for series uid:"+dto.getSeriesUID());
        }

        im.setSeries(series);
        im.setLoginName(dto.getUsrLoginName());
        im.setMarkupContent(dto.getMarkupData());
        im.setSeriesInstanceUID(dto.getSeriesUID());
        im.setSubmissionDate(new java.util.Date());

        try {
            session.save(im);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            session.flush();
            session.close();
        }
    }


    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#markupExist(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)	
    public boolean markupExist(MarkupDTO dto) throws DataAccessException {
        String hql= "select distinct imgmkup.id from ImageMarkup imgmkup where ";
        hql = hql + "imgmkup.seriesInstanceUID = '" + dto.getSeriesUID() + "'";


        List results = getHibernateTemplate().find(hql);
        System.out.println("Done get markup ids");

        if (results != null && results.size() > 0) {
            return true;
        }
        else {
        	return false;
        }
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncia.markup.MarkupManager#isDuplicate(gov.nih.nci.ncia.dto.MarkupDTO)
	 */
	@Transactional(propagation=Propagation.REQUIRED)	
    public boolean isDuplicate(MarkupDTO dto) throws DataAccessException {

        String hql= "select distinct imgmkup.id from ImageMarkup imgmkup where ";
        hql = hql + "imgmkup.seriesInstanceUID = '" + dto.getSeriesUID() + "'";
        //hql = hql + " and imgmkup.loginName = '" + dto.getUsrLoginName() + "'";

        List results = getHibernateTemplate().find(hql);

        if (results != null && results.size() > 0) {
            return true;
        }
        else {
        	return false;
        }
    }
}