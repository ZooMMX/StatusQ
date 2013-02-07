package controllers;

import models.Producto;
import models.Sucursal;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;

import java.util.HashMap;
import java.util.List;

@With(Secure.class)
public class Productos extends Application {


    public static void list(Long sucursal_id) {
        //List<Producto> Productos = Producto.all().from(0).fetch(50);
        //renderArgs.put("Productos", Productos);
        Sucursal suc = Sucursal.findById(sucursal_id);
        renderArgs.put("tab"        , suc.nombre);
        renderArgs.put("sucursal_id", suc.id);
        renderArgs.put("titulo"     , "Producto de sucursal " + suc.nombre);
        render();
    }

    public static void listJson() {
        final DataTablesParameters dtp = new DataTablesParameters(request);
        Long countProductos = Producto.count("sucursal_id = ?", dtp.sucursal_id);

        List<Producto> productos = Producto.find(
                "sucursal_id = ? and (nombre like ? or codigo like ?)",
                    dtp.sucursal_id,
                    "%"+dtp.search+"%",
                    "%"+dtp.search+"%"
                )
                .from(
                    ((dtp.page-1)*dtp.pageSize)+1)
                .fetch(dtp.pageSize);
        Long filteredRows = Producto.count(
                "sucursal_id = ? and (nombre like ? or codigo like ?)",
                    dtp.sucursal_id,
                    "%"+dtp.search+"%",
                    "%"+dtp.search+"%"
                );

        String[][] pArray = new String[productos.size()][4];

        for(int i = 0; i < productos.size(); i++) {
            pArray[i][0] = productos.get(i).codigo;
            pArray[i][1] = productos.get(i).nombre;
            pArray[i][2] = productos.get(i).costo.toString();
            pArray[i][3] = productos.get(i).precio.toString();
        }
        HashMap<String, Object> pHash = new HashMap<String, Object>();
        pHash.put("aaData"              , pArray);
        pHash.put("iTotalRecords"       , String.valueOf(countProductos));
        pHash.put("iTotalDisplayRecords", String.valueOf(filteredRows));


        renderJSON(pHash);
    }

    public static class DataTablesParameters {
      public int page;
      public Integer pageSize = 0;
      public String search;
      public String orderBy;
      public String order;
      public Long sucursal_id;


      public DataTablesParameters(final Http.Request request) {

         // Paging
         final Integer startRow = request.params.get("iDisplayStart", Integer.class);
         this.pageSize = request.params.get("iDisplayLength", Integer.class);
         this.page = startRow == null ? 1 : (startRow / this.pageSize) + 1;

         // Sorting (first sort column only).
         final Integer sortingColumns = request.params.get("iSortingCols", Integer.class);
         final Long sucursal_id   = Long.valueOf(request.params.get("sucursal_id"));
         this.sucursal_id = sucursal_id;

         if (sortingColumns != null) {
            final int sortColumnIndex  = request.params.get("iSortCol_0", Integer.class);
            final String sortDirection = request.params.get("sSortDir_0");
            //this.orderBy = type.getFields().get(sortColumnIndex).name;
            //this.order = sortDirection == null ? null : sortDirection.toUpperCase();
         }

         // Search
         this.search = request.params.get("sSearch");
      }
    }


}
