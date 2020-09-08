docker-compose up --build --remove-orphans --force-recreate --detach;

Write-Host "Containers are now running.";
Write-Host "Use 'docker container ls' to show a list of active containers."
Write-Host "Use 'docker logs <CONTAINER> --follow' to display a live output of a container's console."
Write-Host "Press any key to stop the containers...";
$Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown");

Write-Host "Shutting down containers...";
docker-compose down --remove-orphans;
