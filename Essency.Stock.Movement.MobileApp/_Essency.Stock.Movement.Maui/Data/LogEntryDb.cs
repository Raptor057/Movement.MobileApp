using Essency.Stock.Movement.Maui.Models.AppConfigurations;

namespace Essency.Stock.Movement.Maui.Data
{
    public class LogEntryDb : SQLiteConnection
    {
        public async Task<List<LogEntry>> GetAllLogEntriesOrdered()
        {
            try
            {
                await InitDatabase();

                var logs = await Database.Table<LogEntry>()
                                          .OrderByDescending(l => l.Timestamp)
                                          .ToListAsync()
                                          .ConfigureAwait(false);
                return logs;
            }
            catch (Exception ex)
            {
                throw new Exception($"An error occurred while retrieving log entries. \n{ex.Message}");
            }
        }
    }
}
