vif=$(vif --list | grep $2)
if [ -z "$vif" ]
then
    container=$(docker ps | grep contrail-vrouter-agent | awk '{print $1}')
    docker exec $container python /opt/contrail/utils/provision_vgw_interface.py --oper $1 --interface $2 --subnets $3 --routes $4 --vrf $5
fi