using Essency.Stock.Movement.Maui.Models;
using SQLite;

namespace Essency.Stock.Movement.Maui.Data
{
    public class StockListDb : SQLiteConnection
    {
        public async Task<List<StockList>> GetStockList()
        {
            try
            {
                await InitDatabase();
                return await Database.Table<StockList>().ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the stock list. \n{ex.Message}");
            }
        }

        public async Task<StockList> GetStockById(int id)
        {
            try
            {
                await InitDatabase();
                return await Database.Table<StockList>().FirstOrDefaultAsync(s => s.ID == id);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the stock by ID. \n{ex.Message}");
            }
        }

        public async Task InsertStock(StockList stock)
        {
            try
            {
                await InitDatabase();

                if (stock == null)
                    throw new ArgumentNullException(nameof(stock), "Stock data cannot be null.");

                await Database.InsertAsync(stock);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while inserting the stock. \n{ex.Message}");
            }
        }

        public async Task UpdateStock(StockList stock)
        {
            try
            {
                await InitDatabase();

                if (stock == null)
                    throw new ArgumentNullException(nameof(stock), "Stock data cannot be null.");

                var existingStock = await Database.Table<StockList>().FirstOrDefaultAsync(s => s.ID == stock.ID);

                if (existingStock == null)
                    throw new InvalidOperationException($"Stock with ID {stock.ID} does not exist.");

                existingStock.IDStock = stock.IDStock;
                existingStock.Company = stock.Company;
                existingStock.Source = stock.Source;
                existingStock.SoucreLoc = stock.SoucreLoc;
                existingStock.Destination = stock.Destination;
                existingStock.DestinationLoc = stock.DestinationLoc;
                existingStock.PartNo = stock.PartNo;
                existingStock.Rev = stock.Rev;
                existingStock.Lot = stock.Lot;
                existingStock.Qty = stock.Qty;
                existingStock.Date = stock.Date;
                existingStock.TimeStamp = stock.TimeStamp;
                existingStock.User = stock.User;
                existingStock.ContBolNum = stock.ContBolNum;

                await Database.UpdateAsync(existingStock);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while updating the stock. \n{ex.Message}");
            }
        }

        public async Task DeleteStock(int id)
        {
            try
            {
                await InitDatabase();

                var stock = await Database.Table<StockList>().FirstOrDefaultAsync(s => s.ID == id);

                if (stock == null)
                    throw new InvalidOperationException($"Stock with ID {id} does not exist.");

                await Database.DeleteAsync(stock);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while deleting the stock. \n{ex.Message}");
            }
        }
    }
}
