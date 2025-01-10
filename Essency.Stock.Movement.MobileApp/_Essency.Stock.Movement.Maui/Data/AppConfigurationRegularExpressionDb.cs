using Essency.Stock.Movement.Maui.Models;

namespace Essency.Stock.Movement.Maui.Data
{
    public class AppConfigurationRegularExpressionDb : SQLiteConnection
    {
        public async Task<List<AppConfigurationRegularExpression>> GetRegularExpressionList()
        {
            await InitDatabase();
            var RegularExpressionList = await Database.Table<AppConfigurationRegularExpression>().ToListAsync().ConfigureAwait(false);
            
            if(RegularExpressionList.Count == 0)
            {
                throw new Exception($"Regular Expression Not Fount, Pleace First Added Data.");
            }

            return RegularExpressionList;
        }

        public async Task UpdateRegularExpression(int id, string RegularExpression)
        {
            await InitDatabase();

            // Recuperar el registro que cumple con la condición
            var itemToUpdate = await Database.Table<AppConfigurationRegularExpression>()
                                              .FirstOrDefaultAsync(D => D.ID == id);
            if (itemToUpdate == null)
            {
                throw new Exception($"List of regular expressions not found, cannot scan labels, contact developer");
            }

            if (itemToUpdate != null)
            {
                // Modificar el valor deseado
                itemToUpdate.RegularExpression = RegularExpression;

                // Actualizar el registro en la base de datos
                await Database.UpdateAsync(itemToUpdate);
            }
        }
    }
}
