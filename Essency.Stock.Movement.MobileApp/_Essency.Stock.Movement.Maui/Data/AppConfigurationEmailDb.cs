using Essency.Stock.Movement.Maui.Models;

namespace Essency.Stock.Movement.Maui.Data
{
    public class AppConfigurationEmailDb : SQLiteConnection
    {
        public async Task<string> GetEmail()
        {
            try
            {
                await InitDatabase();
                var Email = await Database.Table<AppConfigurationEmail>().FirstOrDefaultAsync().ConfigureAwait(false);
                return Email.Email.ToString().ToLower() ?? "";
            }
            catch (Exception ex)
            {
                // Manejo de error genérico
                throw new Exception($"An error occurred, Please try again. \n{ex.Message}");
            }
        }

        public async Task InsertOrUpdateEmail(string Email)
        {
            try
            {
                await InitDatabase();

                var email = await GetEmail();

                if (string.IsNullOrEmpty(email))
                {
                    await Database.InsertAsync(Email.ToLower().Trim()).ConfigureAwait(false);
                }
                else
                {
                    await Database.UpdateAsync(Email.ToLower().Trim()).ConfigureAwait(false);
                }
            }
            catch (Exception ex)
            {
                // Manejo de error genérico
                throw new Exception($"An error occurred, Please try again. \n{ex.Message}");
            }
        }

    }
}
