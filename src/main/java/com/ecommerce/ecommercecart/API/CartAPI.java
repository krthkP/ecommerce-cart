package com.ecommerce.ecommercecart.API;

import com.ecommerce.ecommercecart.database.SqliteDB;
import com.ecommerce.ecommercecart.model.Cart;
import com.ecommerce.ecommercecart.model.CartParams;
import com.ecommerce.ecommercecart.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


@RestController
public class CartAPI {

    Cart cart = new Cart();
    SqliteDB sqliteDB = new SqliteDB();

    @RequestMapping(value = "/addToCart", method = RequestMethod.POST)
    public String addToCart(@RequestBody CartParams cartParams) throws IOException, SQLException, ClassNotFoundException {

        cart.setItemid(cartParams.getItemID());
        cart.setName(cartParams.getName());
        cart.setQuanity(cartParams.getQuantity());
        cart.setUser(cartParams.getUser());

        sqliteDB.addCartData(cart);

        return "Item added to the cart Successfully";
    }

    @RequestMapping(value = "/getCartData", method = RequestMethod.GET)
    public String getCartData(@RequestParam(value = "user", required = true) String user) throws SQLException, ClassNotFoundException {

        return getDataFromDB(user).toString();
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkout(@RequestParam(value = "user", required = true) String user) throws SQLException, ClassNotFoundException, IOException {

        int totalBill = 0;
        int totalPromos = 0;

        ProductAPI productAPI = new ProductAPI();

        JSONArray jsonArray = getDataFromDB(user);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Product product = productAPI.getProductByID(jsonObject.getInt("itemid"));

            if (!product.getPromotions().isEmpty()) {

                if (product.getPromotions().get(0).getType().equalsIgnoreCase("BUY_1_GET_1")) {

                    if (jsonObject.getInt("quantity") > product.getPromotions().get(0).getRequiredQuantity()) {

                        totalBill += (jsonObject.getInt("quantity") * product.getPrice());
                        totalPromos += product.getPrice();
                    } else {
                        totalBill += (jsonObject.getInt("quantity") * product.getPrice());
                        totalPromos += 0;
                    }

                } else if (product.getPromotions().get(0).getType().equalsIgnoreCase("FLAT_DISCOUNT")) {

                    totalBill += (jsonObject.getInt("quantity") * product.getPrice());
                    totalPromos += (jsonObject.getInt("quantity") * product.getPromotions().get(0).getAmount());
                } else if (product.getPromotions().get(0).getType().equalsIgnoreCase("PRICE_DISCOUNT")) {

                    if (jsonObject.getInt("quantity") > product.getPromotions().get(0).getRequiredQuantity()) {

                        totalBill += (jsonObject.getInt("quantity") * product.getPromotions().get(0).getPrice());
                        totalPromos += ((jsonObject.getInt("quantity") * product.getPrice()) - (jsonObject.getInt("quantity") * (product.getPromotions().get(0).getPrice() / 2)));
                    } else {
                        totalBill += (jsonObject.getInt("quantity") * product.getPrice());
                        totalPromos += 0;
                    }
                }
            } else {
                totalBill += jsonObject.getInt("quantity") * product.getPrice();
                totalPromos += 0;
            }
        }

        JSONArray jsonArray1 = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalbill", totalBill);
        jsonObject.put("totalpromos", totalPromos);
        jsonObject.put("totalpayable", (totalBill - totalPromos));

        jsonArray1.put(jsonObject);

        sqliteDB.deleteCartData(user);
        return jsonArray1.toString();
    }

    @RequestMapping(value = "/cartCleanup", method = RequestMethod.GET)
    public String cartCleanup(@RequestParam(value = "user", required = true) String user){

        try {
            sqliteDB.deleteCartData(user);
            return "Cart reset Successful";

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "Cart reset Failed";

    }

    private JSONArray getDataFromDB(String user) throws SQLException, ClassNotFoundException {
        JSONArray jsonArray = new JSONArray();

        ResultSet resultSet = sqliteDB.getCartData(user);
        while (resultSet.next()) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("quantity", resultSet.getInt("quantity"));
            jsonObject.put("name", resultSet.getString("name"));
            jsonObject.put("itemid", resultSet.getString("itemid"));

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
