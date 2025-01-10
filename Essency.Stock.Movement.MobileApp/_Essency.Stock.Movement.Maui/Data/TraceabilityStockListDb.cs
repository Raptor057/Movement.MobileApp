using Essency.Stock.Movement.Maui.Models;
using SQLite;

namespace Essency.Stock.Movement.Maui.Data
{
    public class TraceabilityStockListDb : SQLiteConnection
    {
        public async Task<List<TraceabilityStockList>> GetTraceabilityStockList()
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the history list. \n{ex.Message}");
            }
        }

        public async Task<TraceabilityStockList> GetTraceabilityStockList(int id)
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().FirstOrDefaultAsync(h => h.ID == id);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the history by ID. \n{ex.Message}");
            }
        }

        public async Task<List<TraceabilityStockList>> GetTraceabilityStockList(long IDStock)
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().Where(h => h.IDStock == IDStock).ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the history by ID. \n{ex.Message}");
            }
        }

        public async Task<List<TraceabilityStockList>> GetTraceabilityStockList(DateTime startDatetime, DateTime endDatetime)
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().Where(h => h.TimeStamp >= startDatetime && h.TimeStamp <= endDatetime).ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving the history by ID. \n{ex.Message}");
            }
        }

        public async Task InsertHistory(TraceabilityStockList history)
        {
            try
            {
                await InitDatabase();

                if (history == null)
                    throw new ArgumentNullException(nameof(history), "History data cannot be null.");

                await Database.InsertAsync(history);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while inserting the history. \n{ex.Message}");
            }
        }

        public async Task UpdateHistory(TraceabilityStockList history)
        {
            try
            {
                await InitDatabase();

                if (history == null)
                    throw new ArgumentNullException(nameof(history), "History data cannot be null.");

                var existingHistory = await Database.Table<TraceabilityStockList>().FirstOrDefaultAsync(h => h.ID == history.ID);

                if (existingHistory == null)
                    throw new InvalidOperationException($"History with ID {history.ID} does not exist.");

                existingHistory.IDStock = history.IDStock;
                existingHistory.Saved = history.Saved;
                existingHistory.SendByEmail = history.SendByEmail;

                await Database.UpdateAsync(existingHistory);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while updating the history. \n{ex.Message}");
            }
        }

        public async Task DeleteHistory(int id)
        {
            try
            {
                await InitDatabase();

                var history = await Database.Table<TraceabilityStockList>().FirstOrDefaultAsync(h => h.ID == id);

                if (history == null)
                    throw new InvalidOperationException($"History with ID {id} does not exist.");

                await Database.DeleteAsync(history);
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while deleting the history. \n{ex.Message}");
            }
        }

        public async Task<List<TraceabilityStockList>> GetUnsavedHistories()
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().Where(h => !h.Saved).ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving unsaved histories. \n{ex.Message}");
            }
        }

        public async Task<List<TraceabilityStockList>> GetHistoriesToSendByEmail()
        {
            try
            {
                await InitDatabase();
                return await Database.Table<TraceabilityStockList>().Where(h => h.SendByEmail).ToListAsync();
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving histories to send by email. \n{ex.Message}");
            }
        }
    }
}
